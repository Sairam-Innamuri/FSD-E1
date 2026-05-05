// my-events.js - My Events page logic

let currentStudentId = null;

function lookupEvents() {
    const email = document.getElementById('lookup-email').value.trim();
    document.getElementById('lookup-err').textContent = '';
    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        document.getElementById('lookup-err').textContent = 'Enter a valid email address.';
        return;
    }

    const spinner = document.getElementById('lookup-spinner');
    const text = document.getElementById('lookup-text');
    spinner.style.display = 'block';
    text.style.display = 'none';

    fetch(`/api/my-events?email=${encodeURIComponent(email)}`)
        .then(r => r.json())
        .then(res => {
            spinner.style.display = 'none';
            text.style.display = 'inline';

            const resultDiv = document.getElementById('events-result');
            const listDiv = document.getElementById('my-events-list');
            resultDiv.style.display = 'block';

            if (!res.success || !res.data || res.data.length === 0) {
                listDiv.innerHTML = `
                    <div class="empty-state">
                        <div class="empty-icon">📭</div>
                        <div class="empty-title">No events found</div>
                        <div class="empty-desc">You haven't registered for any events yet with this email.</div>
                        <a href="/" class="btn btn-primary" style="margin-top:1rem;">Browse Events</a>
                    </div>`;
                return;
            }

            const regs = res.data;
            if (regs.length > 0 && regs[0].student) {
                currentStudentId = regs[0].student.id;
            }

            listDiv.innerHTML = `<div class="section-title" style="margin-bottom:1rem;">
                Found <span>${regs.length} event(s)</span></div>
                <div class="events-grid">` +
                regs.map(reg => {
                    const ev = reg.event;
                    const dateStr = ev.date ? new Date(ev.date).toLocaleDateString('en-IN', {
                        weekday: 'short', day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
                    }) : 'TBD';
                    return `
                    <div class="event-card">
                        <div class="event-card-header">
                            <div class="event-type-badge badge-workshop">${ev.type || 'Event'}</div>
                            <h3 class="event-title">${ev.title}</h3>
                            <div class="event-meta">
                                <div class="event-meta-item">📅 ${dateStr}</div>
                                <div class="event-meta-item">📍 ${ev.venue || 'TBD'}</div>
                                <div class="event-meta-item">🏛 ${ev.department || ''}</div>
                            </div>
                            <span class="alert alert-success" style="display:inline-flex;padding:.25rem .6rem;font-size:.78rem;">✅ Registered</span>
                        </div>
                        <div class="event-card-footer">
                            <button class="btn btn-secondary btn-sm" onclick="openFeedback(${ev.id}, ${currentStudentId})">📝 Feedback</button>
                        </div>
                    </div>`;
                }).join('') + '</div>';
        })
        .catch(() => {
            document.getElementById('lookup-spinner').style.display = 'none';
            document.getElementById('lookup-text').style.display = 'inline';
            showToast('Could not load events. Try again.', 'error');
        });
}

function openFeedback(eventId, studentId) {
    document.getElementById('fb-event-id').value = eventId;
    document.getElementById('fb-student-id').value = studentId;
    document.getElementById('fb-comments').value = '';
    document.getElementById('fb-err').textContent = '';
    document.querySelectorAll('.rating-input input').forEach(r => r.checked = false);
    const overlay = document.getElementById('feedback-overlay');
    overlay.style.display = 'flex';
}

function closeFeedback() {
    document.getElementById('feedback-overlay').style.display = 'none';
}

function submitFeedback() {
    const eventId = document.getElementById('fb-event-id').value;
    const studentId = document.getElementById('fb-student-id').value;
    const comments = document.getElementById('fb-comments').value.trim();
    const ratingEl = document.querySelector('.rating-input input:checked');
    document.getElementById('fb-err').textContent = '';

    if (!ratingEl) { document.getElementById('fb-err').textContent = 'Please select a rating.'; return; }
    if (!comments || comments.length < 10) { document.getElementById('fb-err').textContent = 'Comments must be at least 10 characters.'; return; }

    fetch('/api/feedback', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ eventId: parseInt(eventId), studentId: parseInt(studentId), comments, rating: parseInt(ratingEl.value) })
    })
    .then(r => r.json())
    .then(res => {
        if (res.success) {
            closeFeedback();
            showToast('Feedback submitted! Thank you.', 'success');
        } else {
            document.getElementById('fb-err').textContent = res.message;
        }
    })
    .catch(() => showToast('Could not submit feedback. Try again.', 'error'));
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
