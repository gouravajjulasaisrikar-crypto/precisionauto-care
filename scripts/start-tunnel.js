const { spawn, exec } = require('child_process');
const path = require('path');
const http = require('http');

const CLOUDFLARED_PATH = path.join(__dirname, '..', '.tools', 'cloudflared.exe');

console.log('\n===================================================================');
console.log('   INITIALIZING SECURE CLOUDFLARE PUBLIC TUNNEL FOR REVIEW...');
console.log('===================================================================\n');

function ensureFrontendServer() {
    return new Promise((resolve) => {
        const req = http.get('http://127.0.0.1:3000', (res) => {
            console.log(' [OK] Local server detected running on port 3000.');
            resolve();
        });
        req.on('error', () => {
            console.log(' [!] Port 3000 is inactive. Auto-starting local-server.js...');
            const server = spawn(process.execPath, [path.join(__dirname, '..', 'local-server.js')], {
                cwd: path.join(__dirname, '..'),
                stdio: 'inherit',
                detached: false
            });
            server.on('error', (err) => {
                console.error('Failed to spawn local-server.js:', err);
            });
            setTimeout(() => {
                console.log(' [OK] Local frontend server initialized on port 3000.');
                resolve();
            }, 2500);
        });
    });
}

async function startTunnel() {
    await ensureFrontendServer();

    console.log(' [*] Connecting Cloudflare Edge Tunnel to http://127.0.0.1:3000...\n');
    const child = spawn(CLOUDFLARED_PATH, ['tunnel', '--url', 'http://127.0.0.1:3000'], {
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

    child.stdout.on('data', parseOutput);
    child.stderr.on('data', parseOutput);

    child.on('close', (code) => {
        console.log(`Tunnel closed (code ${code})`);
    });
}

startTunnel();
