const { spawn, exec, execSync } = require('child_process');
const path = require('path');
const http = require('http');

const CLOUDFLARED_PATH = path.join(__dirname, '..', '.tools', 'cloudflared.exe');

console.log('\n===================================================================');
console.log('   INITIALIZING SECURE CLOUDFLARE PUBLIC TUNNEL FOR REVIEW...');
console.log('===================================================================\n');

// 1. Terminate any previous orphaned cloudflared processes
try {
    execSync('taskkill /F /IM cloudflared.exe 2>nul', { stdio: 'ignore' });
} catch (e) {}

function checkPort3000() {
    return new Promise((resolve) => {
        const req = http.get('http://127.0.0.1:3000', (res) => {
            resolve(res.statusCode === 200);
        });
        req.on('error', () => resolve(false));
        req.setTimeout(1000, () => {
            req.destroy();
            resolve(false);
        });
    });
}

async function ensureFrontendServer() {
    const isRunning = await checkPort3000();
    if (isRunning) {
        console.log(' [OK] Local frontend server is active on port 3000.');
        return;
    }

    console.log(' [!] Port 3000 is inactive. Spawning local-server.js...');
    const serverProcess = spawn(process.execPath, [path.join(__dirname, '..', 'local-server.js')], {
        cwd: path.join(__dirname, '..'),
        stdio: 'inherit',
        detached: false
    });

    serverProcess.on('error', (err) => {
        console.error('Failed to spawn local-server.js:', err);
    });

    // Poll until port 3000 is ready (up to 15 seconds)
    for (let i = 0; i < 30; i++) {
        await new Promise(r => setTimeout(r, 500));
        if (await checkPort3000()) {
            console.log(' [OK] Local frontend server is now ready on port 3000.');
            return;
        }
    }
    console.warn(' [!] Warning: Server spawn timed out, continuing tunnel initialization...');
}

let cloudflaredChild = null;

function cleanup() {
    if (cloudflaredChild) {
        try {
            cloudflaredChild.kill();
        } catch (e) {}
    }
    try {
        execSync('taskkill /F /IM cloudflared.exe 2>nul', { stdio: 'ignore' });
    } catch (e) {}
}

process.on('SIGINT', () => { cleanup(); process.exit(0); });
process.on('SIGTERM', () => { cleanup(); process.exit(0); });
process.on('exit', () => cleanup());

async function startTunnel() {
    await ensureFrontendServer();

    console.log(' [*] Establishing Cloudflare Edge Tunnel -> http://127.0.0.1:3000...\n');
    cloudflaredChild = spawn(CLOUDFLARED_PATH, ['tunnel', '--url', 'http://127.0.0.1:3000', '--no-autoupdate'], {
        stdio: ['ignore', 'pipe', 'pipe']
    });

    let urlFound = false;

    function parseOutput(data) {
        const text = data.toString();
        const match = text.match(/https:\/\/[a-z0-9-]+\.trycloudflare\.com/i);
        if (match && !urlFound) {
            urlFound = true;
            const publicUrl = match[0];
            console.log('===================================================================');
            console.log('   PUBLIC ACCESS LINK IS LIVE (SHARE THIS WITH FACULTY / SIR)!');
            console.log('===================================================================');
            console.log('');
            console.log(`   PUBLIC URL:  \x1b[32m\x1b[1m${publicUrl}\x1b[0m`);
            console.log('');
            console.log('   * Anyone with this link can open and test it from anywhere');
            console.log('   * Works on mobile (4G/5G), other laptops, college Wi-Fi');
            console.log('   * End-to-end HTTPS encrypted via Cloudflare Edge Network');
            console.log('   * Real Google SMTP OTP dispatched in 2-3 seconds');
            console.log('   * All microservices (Auth, Booking, Records, Bills) work through it');
            console.log('===================================================================\n');
            
            // Copy to Windows clipboard
            try {
                const clip = spawn('clip', { stdio: ['pipe', 'ignore', 'ignore'] });
                clip.stdin.end(publicUrl);
                console.log(' [OK] Link copied to your clipboard! Just press Ctrl+V to share.\n');
            } catch (e) {}

            // Auto-open in browser
            try {
                exec(`start "" "${publicUrl}"`);
            } catch (e) {}
        }
    }

    cloudflaredChild.stdout.on('data', parseOutput);
    cloudflaredChild.stderr.on('data', parseOutput);

    cloudflaredChild.on('close', (code) => {
        console.log(`Tunnel closed (code ${code})`);
    });
}

startTunnel();
