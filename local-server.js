const http = require('http');
const fs = require('fs');
const path = require('path');
const os = require('os');
const nodemailer = require('nodemailer');

const PORT = 3000;
const PUBLIC_DIR = path.join(__dirname, 'frontend');
const GATEWAY_TARGET = {
    host: '127.0.0.1',
    port: 8080
};

// Verified Gmail SMTP Transporter for Real OTP Delivery
const mailTransporter = nodemailer.createTransport({
    host: 'smtp.gmail.com',
    port: 587,
    secure: false,
    auth: {
        user: 'gouravajjulasaisrikar@gmail.com',
        pass: 'zlfwczrgnpwxqzwf'
    }
});

const MIME_TYPES = {
    '.html': 'text/html',
    '.css': 'text/css',
    '.js': 'text/javascript',
    '.json': 'application/json',
    '.png': 'image/png',
    '.jpg': 'image/jpeg',
    '.svg': 'image/svg+xml'
};

const server = http.createServer((req, res) => {
    // 1. Real Email Dispatch Endpoint via Google SMTP
    if (req.url === '/api/send-otp-email' && req.method === 'POST') {
        let body = '';
        req.on('data', chunk => { body += chunk; });
        req.on('end', async () => {
            try {
                const { to, otp, name } = JSON.parse(body || '{}');
                const targetEmail = (to && to.includes('@') && !to.endsWith('@precisionauto.com')) 
                    ? to 
                    : 'gouravajjulasaisrikar@gmail.com';

                console.log(`[SMTP Mailer] Dispatching REAL OTP ${otp} to ${targetEmail}...`);

                const mailInfo = await mailTransporter.sendMail({
                    from: '"PrecisionAuto Care Security" <gouravajjulasaisrikar@gmail.com>',
                    to: targetEmail,
                    subject: 'PrecisionAuto Care • Login Verification Code: ' + otp,
                    html: `
                        <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #060913; color: #ffffff; padding: 32px; border-radius: 12px; max-width: 520px; margin: auto; border: 1px solid rgba(56, 189, 248, 0.3);">
                            <div style="text-align: center; margin-bottom: 24px;">
                                <h2 style="color: #38bdf8; margin: 0; font-size: 24px; letter-spacing: 0.5px;">PrecisionAuto Care</h2>
                                <p style="color: #94a3b8; font-size: 13px; margin-top: 4px;">Zero-Trust Identity Gateway &bull; Two-Factor Authentication</p>
                            </div>
                            <div style="background: rgba(15, 23, 42, 0.7); border: 1px solid #1e293b; border-radius: 8px; padding: 20px; margin-bottom: 20px;">
                                <p style="margin-top: 0; color: #e2e8f0; font-size: 15px;">Hello <strong>${name || 'User'}</strong>,</p>
                                <p style="color: #cbd5e1; font-size: 14px; line-height: 1.5;">Your one-time security authorization code (OTP) for accessing the PrecisionAuto Care platform is:</p>
                                <div style="background: #0b1120; border: 2px solid #38bdf8; border-radius: 8px; padding: 18px; text-align: center; font-size: 32px; font-weight: 800; letter-spacing: 8px; color: #38bdf8; margin: 20px 0; font-family: monospace; text-shadow: 0 0 15px rgba(56, 189, 248, 0.5);">
                                    ${otp}
                                </div>
                                <p style="color: #94a3b8; font-size: 12px; margin-bottom: 0;">&bull; This code is valid for <strong>120 seconds</strong>.<br>&bull; Never share this code with anyone, including workshop staff.</p>
                            </div>
                            <p style="font-size: 11px; color: #64748b; text-align: center; margin: 0;">Secured by PrecisionAuto Care &bull; Spring Cloud API Gateway</p>
                        </div>
                    `
                });

                console.log(`[SMTP Mailer] Email sent successfully! MessageId: ${mailInfo.messageId}`);
                res.writeHead(200, {
                    'Content-Type': 'application/json',
                    'Access-Control-Allow-Origin': '*'
                });
                res.end(JSON.stringify({
                    status: 'DELIVERED',
                    messageId: mailInfo.messageId,
                    recipient: targetEmail
                }));
            } catch (err) {
                console.error('[SMTP Mailer Error]', err.message);
                res.writeHead(500, {
                    'Content-Type': 'application/json',
                    'Access-Control-Allow-Origin': '*'
                });
                res.end(JSON.stringify({ status: 'ERROR', message: err.message }));
            }
        });
        return;
    }

    // 2. API Reverse Proxy: Forward all /api/* requests to Spring Cloud API Gateway (Port 8080)
    // This allows public access (Cloudflare / Pinggy / Mobile) through a single port with ZERO CORS or mixed-content issues!
    if (req.url.startsWith('/api')) {
        const proxyHeaders = { ...req.headers };
        proxyHeaders.host = `${GATEWAY_TARGET.host}:${GATEWAY_TARGET.port}`;
        proxyHeaders['x-forwarded-host'] = req.headers.host || '';
        proxyHeaders['x-forwarded-proto'] = req.headers['x-forwarded-proto'] || 'http';

        const proxyReq = http.request({
            host: GATEWAY_TARGET.host,
            port: GATEWAY_TARGET.port,
            path: req.url,
            method: req.method,
            headers: proxyHeaders
        }, (proxyRes) => {
            const resHeaders = { ...proxyRes.headers };
            resHeaders['access-control-allow-origin'] = '*';
            resHeaders['access-control-allow-methods'] = 'GET, POST, PUT, DELETE, OPTIONS';
            resHeaders['access-control-allow-headers'] = '*';
            res.writeHead(proxyRes.statusCode, resHeaders);
            proxyRes.pipe(res);
        });

        proxyReq.on('error', (err) => {
            res.writeHead(502, { 
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*'
            });
            res.end(JSON.stringify({
                status: 502,
                error: 'Bad Gateway',
                message: 'API Gateway (Port 8080) unreachable. Please ensure microservices are running.'
            }));
        });

        req.pipe(proxyReq);
        return;
    }

    // 2. Static File Serving
    let safePath = path.normalize(req.url.split('?')[0]);
    if (safePath === '/' || safePath === '\\') safePath = '/index.html';
    const filePath = path.join(PUBLIC_DIR, safePath);

    fs.readFile(filePath, (err, content) => {
        if (err) {
            res.writeHead(404, { 'Content-Type': 'text/plain' });
            res.end('File not found');
            return;
        }
        const ext = path.extname(filePath).toLowerCase();
        res.writeHead(200, {
            'Content-Type': MIME_TYPES[ext] || 'application/octet-stream',
            'Access-Control-Allow-Origin': '*'
        });
        res.end(content);
    });
});

function getLocalIp() {
    const interfaces = os.networkInterfaces();
    for (const name of Object.keys(interfaces)) {
        for (const iface of interfaces[name]) {
            if (iface.family === 'IPv4' && !iface.internal) {
                return iface.address;
            }
        }
    }
    return '127.0.0.1';
}

server.listen(PORT, '0.0.0.0', () => {
    const localIp = getLocalIp();
    console.log(`====================================================`);
    console.log(` PrecisionAuto Care Frontend & API Proxy Live!`);
    console.log(` Local access:   http://localhost:${PORT}`);
    console.log(` LAN / Mobile:   http://${localIp}:${PORT}`);
    console.log(` Gateway Target: http://${GATEWAY_TARGET.host}:${GATEWAY_TARGET.port}`);
    console.log(`====================================================`);
});
