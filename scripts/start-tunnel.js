const { spawn, exec } = require('child_process');
const path = require('path');

const CLOUDFLARED_PATH = path.join(__dirname, '..', '.tools', 'cloudflared.exe');

console.log('\n===================================================================');
console.log('   INITIALIZING SECURE CLOUDFLARE PUBLIC TUNNEL FOR REVIEW...');
console.log('===================================================================\n');

const child = spawn(CLOUDFLARED_PATH, ['tunnel', '--url', 'http://localhost:3000'], {
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
