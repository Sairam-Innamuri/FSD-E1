// register.js - OTP Registration Flow

let countdownInterval = null;

function setLoading(btnId, spinnerId, textId, loading) {
    document.getElementById(spinnerId).style.display = loading ? 'block' : 'none';
    document.getElementById(textId).style.display = loading ? 'none' : 'inline';
    document.getElementById(btnId).disabled = loading;
}

function sendOtp() {
    const name = document.getElementById('reg-name').value.trim();
    const email = document.getElementById('reg-email').value.trim();
    const dept = document.getElementById('reg-dept').value;
    let valid = true;

    document.getElementById('err-name').textContent = '';
    document.getElementById('err-email').textContent = '';
    document.getElementById('err-dept').textContent = '';

    if (!name || name.length < 2) { document.getElementById('err-name').textContent = 'Name must be at least 2 characters.'; valid = false; }
    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) { document.getElementById('err-email').textContent = 'Enter a valid email.'; valid = false; }
    if (!dept) { document.getElementById('err-dept').textContent = 'Please select your department.'; valid = false; }
    if (!valid) return;

    setLoading('send-otp-btn', 'otp-spinner', 'otp-btn-text', true);

    fetch('/api/otp/send', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, eventId: EVENT_ID })
    })
    .then(r => r.json())
    .then(res => {
        setLoading('send-otp-btn', 'otp-spinner', 'otp-btn-text', false);
        if (res.success) {
            document.getElementById('step1').style.display = 'none';
            document.getElementById('step2').style.display = 'block';
            document.getElementById('otp-sent-msg').textContent = `✅ OTP sent to ${email}. Check your inbox (or server console in dev mode).`;
            document.getElementById('step2-ind').classList.add('active');
            startCountdown(300);
        } else {
            showToast(res.message, 'error');
        }
    })
    .catch(() => {
        setLoading('send-otp-btn', 'otp-spinner', 'otp-btn-text', false);
        showToast('Network error. Please try again.', 'error');
    });
}

function verifyOtp() {
    const otp = document.getElementById('otp-code').value.trim();
    const email = document.getElementById('reg-email').value.trim();
    const name = document.getElementById('reg-name').value.trim();
    const dept = document.getElementById('reg-dept').value;

    document.getElementById('err-otp').textContent = '';
    if (!otp || otp.length !== 6) {
        document.getElementById('err-otp').textContent = 'Enter a valid 6-digit OTP.';
        return;
    }

    setLoading('verify-btn', 'verify-spinner', 'verify-text', true);

    fetch('/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, department: dept, eventId: EVENT_ID, otpCode: otp })
    })
    .then(r => r.json())
    .then(res => {
        setLoading('verify-btn', 'verify-spinner', 'verify-text', false);
        if (res.success) {
            document.getElementById('step2').style.display = 'none';
            document.getElementById('step3').style.display = 'block';
            document.getElementById('step3-ind').classList.add('active');
            if (countdownInterval) clearInterval(countdownInterval);
        } else {
            document.getElementById('err-otp').textContent = res.message;
        }
    })
    .catch(() => {
        setLoading('verify-btn', 'verify-spinner', 'verify-text', false);
        showToast('Network error. Please try again.', 'error');
    });
}

function resendOtp() {
    const email = document.getElementById('reg-email').value.trim();
    fetch('/api/otp/send', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, eventId: EVENT_ID })
    })
    .then(r => r.json())
    .then(res => {
        if (res.success) {
            showToast('OTP resent! Check your email.', 'success');
            if (countdownInterval) clearInterval(countdownInterval);
            startCountdown(300);
        } else {
            showToast(res.message, 'error');
        }
    });
}

function startCountdown(seconds) {
    const el = document.getElementById('countdown');
    let remaining = seconds;
    el.textContent = formatTime(remaining);
    countdownInterval = setInterval(() => {
        remaining--;
        el.textContent = formatTime(remaining);
        if (remaining <= 0) {
            clearInterval(countdownInterval);
            el.textContent = 'EXPIRED';
            el.style.color = 'var(--danger)';
        }
    }, 1000);
}

function formatTime(s) {
    const m = Math.floor(s / 60);
    const sec = s % 60;
    return `${String(m).padStart(2,'0')}:${String(sec).padStart(2,'0')}`;
}

function showToast(message, type) {
    const c = document.getElementById('toast-container');
    if (!c) return;
    const t = document.createElement('div');
    t.className = `toast toast-${type}`;
    t.textContent = message;
    c.appendChild(t);
    setTimeout(() => t.remove(), 4000);
}
