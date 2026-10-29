# PrecisionAuto Care: Cloud Deployment Guide (Railway + Vercel + GitHub)

This guide walks you through deploying the complete **PrecisionAuto Care** platform to the cloud:
* **Backend Microservices Mesh**: Deployed on **Railway** via Docker (Eureka, API Gateway, Auth, Booking, Record, Billing).
* **Frontend UI**: Deployed on **Vercel** connected directly to your **GitHub** repository.

---

## 1. Push Your Code to GitHub (30 Seconds)

1. Go to **[https://github.com/new](https://github.com/new)**.
2. Create a repository named `precisionauto-care` (set to **Public** or **Private**).
3. Do **NOT** check "Add README" or ".gitignore" (these are already created and configured).
4. In your project directory terminal, run:
   ```cmd
   git remote add origin https://github.com/YOUR_GITHUB_USERNAME/precisionauto-care.git
   git push -u origin main
   ```

---

## 2. Deploy the Backend Microservices to Railway (2 Minutes)

1. Open **[https://railway.app](https://railway.app)** and sign in with your GitHub account.
2. Click **"+ New Project"** &rarr; Select **"Deploy from GitHub repo"**.
3. Choose your `precisionauto-care` repository.
4. Railway will automatically detect the root `Dockerfile` and `entrypoint.sh`.
5. Click **"Deploy Now"**.
6. Once the build finishes (~2 minutes):
   * Click on your deployed service &rarr; Go to **Settings** &rarr; **Networking**.
   * Click **"Generate Domain"**.
   * Copy your public backend domain (e.g., `https://precisionauto-production.up.railway.app`).

---

## 3. Deploy the Frontend to Vercel (1 Minute)

1. Open **[https://vercel.com/new](https://vercel.com/new)**.
2. Click **"Import"** on your `precisionauto-care` GitHub repository.
3. Under **Root Directory**, click **Edit** and select **`frontend`** (or leave `./` as root `vercel.json` routes to frontend).
4. Click **"Deploy"**.
5. Within 15 seconds, Vercel gives you your live production URL:  
   `https://precisionauto-care.vercel.app`

---

## 4. Connect the Vercel Frontend to the Railway Backend

### Option A: Direct from the Website UI (Instant)
1. Open your live Vercel website: `https://precisionauto-care.vercel.app`.
2. Click the **"Cloud Backend (Railway)"** pill badge at the top of the screen.
3. Paste your Railway backend URL (e.g. `https://precisionauto-production.up.railway.app`).
4. Click **"Test Ping"** &rarr; Status lights up **Connected (Green)**.
5. Click **"Save & Connect"**.

### Option B: Automatic via Vercel Rewrites (`vercel.json`)
Update the `destination` in `frontend/vercel.json` with your Railway URL:
```json
{
  "cleanUrls": true,
  "rewrites": [
    {
      "source": "/api/:path*",
      "destination": "https://YOUR_RAILWAY_URL.up.railway.app/api/:path*"
    }
  ]
}
```
Push to GitHub (`git commit -am "chore: update railway backend url" && git push`), and Vercel auto-deploys in 5 seconds!

---

## 5. Review Day Live Demonstration Flow

1. Open `https://precisionauto-care.vercel.app` on your laptop, mobile phone, or projector.
2. Log in with your email (`gouravajjulasaisrikar@gmail.com` or `customer@precisionauto.com`).
3. Check your phone for the **real Gmail OTP** delivered from `gouravajjulasaisrikar@gmail.com`.
4. Enter the 6-digit code and access the live dashboard.
5. Demonstrate:
   * **Bay 1 Collision Prevention (`HTTP 409 Conflict`)**.
   * **Master Tech Job Completion** with real-time 10% tax calculation preview.
   * **One-Click Bill Settlement** (`PENDING` &rarr; `PAID`).
   * **Admin Topology View** showing all 6 registered microservices.
