const nodemailer = require('nodemailer');

// Verified Google SMTP Transporter for Real OTP Delivery
const mailTransporter = nodemailer.createTransport({
    host: 'smtp.gmail.com',
    port: 587,
    secure: false,
    auth: {
        user: 'gouravajjulasaisrikar@gmail.com',
        pass: 'zlfwczrgnpwxqzwf'
    }
});

module.exports = async (req, res) => {
    // Enable CORS for Vercel Serverless Function
    res.setHeader('Access-Control-Allow-Credentials', 'true');
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET,OPTIONS,PATCH,DELETE,POST,PUT');
    res.setHeader('Access-Control-Allow-Headers', 'X-CSRF-Token, X-Requested-With, Accept, Accept-Version, Content-Length, Content-MD5, Content-Type, Date, X-Api-Version');

    if (req.method === 'OPTIONS') {
        res.status(200).end();
        return;
    }

    if (req.method !== 'POST') {
        return res.status(405).json({ status: 'ERROR', message: 'Method Not Allowed' });
    }

    try {
        // Parse request body if stringified
        let body = req.body;
        if (typeof body === 'string') {
            try { body = JSON.parse(body); } catch (e) {}
        }
        body = body || {};

        const { to, otp, name, action } = body;
        const targetEmail = (to && to.includes('@')) ? to.trim() : 'gouravajjulasaisrikar@gmail.com';

        console.log(`[Vercel Serverless SMTP] Dispatching OTP ${otp} to ${targetEmail}...`);

        const isSignup = (action === 'SIGNUP');
        const mailInfo = await mailTransporter.sendMail({
            from: '"PrecisionAuto Care Security" <gouravajjulasaisrikar@gmail.com>',
            to: targetEmail,
            subject: isSignup 
                ? `PrecisionAuto Care • Account Registration OTP: ${otp}`
                : `PrecisionAuto Care • Login Verification Code: ${otp}`,
            html: `
                <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #060913; color: #ffffff; padding: 32px; border-radius: 12px; max-width: 520px; margin: auto; border: 1px solid rgba(56, 189, 248, 0.3);">
                    <div style="text-align: center; margin-bottom: 24px;">
                        <h2 style="color: #38bdf8; margin: 0; font-size: 24px; letter-spacing: 0.5px;">PrecisionAuto Care</h2>
                        <p style="color: #94a3b8; font-size: 13px; margin-top: 4px;">Zero-Trust Identity Gateway &bull; Two-Factor Authentication</p>
                    </div>
                    <div style="background: rgba(15, 23, 42, 0.7); border: 1px solid #1e293b; border-radius: 8px; padding: 20px; margin-bottom: 20px;">
                        <p style="margin-top: 0; color: #e2e8f0; font-size: 15px;">Hello <strong>${name || 'User'}</strong>,</p>
                        <p style="color: #cbd5e1; font-size: 14px; line-height: 1.5;">${isSignup ? 'Your one-time security authorization code (OTP) to complete your <strong>new account registration</strong> on PrecisionAuto Care is:' : 'Your one-time security authorization code (OTP) for accessing the PrecisionAuto Care platform is:'}</p>
                        <div style="background: #0b1120; border: 2px solid #38bdf8; border-radius: 8px; padding: 18px; text-align: center; font-size: 32px; font-weight: 800; letter-spacing: 8px; color: #38bdf8; margin: 20px 0; font-family: monospace; text-shadow: 0 0 15px rgba(56, 189, 248, 0.5);">
                            ${otp}
                        </div>
                        <p style="color: #94a3b8; font-size: 12px; margin-bottom: 0;">&bull; This code is valid for <strong>120 seconds</strong>.<br>&bull; Sent directly to <strong>${targetEmail}</strong>.<br>&bull; Never share this code with anyone.</p>
                    </div>
                    <p style="font-size: 11px; color: #64748b; text-align: center; margin: 0;">Secured by PrecisionAuto Care &bull; Zero-Trust Mesh</p>
                </div>
            `
        });

        console.log(`[Vercel Serverless SMTP] Message delivered! ID: ${mailInfo.messageId}`);
        return res.status(200).json({
            status: 'DELIVERED',
            messageId: mailInfo.messageId,
            recipient: targetEmail
        });
    } catch (err) {
        console.error('[Vercel Serverless SMTP Error]', err);
        return res.status(500).json({ status: 'ERROR', message: err.message });
    }
};
