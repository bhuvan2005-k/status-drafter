const rawInput = document.getElementById('rawInput');
const btnSubmit = document.getElementById('btnSubmit');
const loader = document.getElementById('loader');
const btnText = document.getElementById('btnText');

const yesterdayList = document.getElementById('yesterdayList');
const todayList = document.getElementById('todayList');
const blockerList = document.getElementById('blockerList');
const hoursSpent = document.getElementById('hoursSpent');
const confidenceScore = document.getElementById('confidenceScore');

const userNameDisplay = document.getElementById('userNameDisplay');
const userAvatar = document.getElementById('userAvatar');
const adminBtn = document.getElementById('adminBtn');

// Fetch user info on load
async function fetchUserInfo() {
    try {
        const response = await fetch('/api/v1/status/me');
        if (!response.ok) throw new Error('Not logged in');
        const user = await response.json();

        userNameDisplay.innerText = `${user.name} (${user.role.replace('ROLE_', '')})`;
        userAvatar.innerText = user.name.charAt(0);

        if (user.role === 'ROLE_ADMIN') {
            adminBtn.style.display = 'block';
            document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'flex');
        } else {
            document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'none');
        }
    } catch (err) {
        window.location.href = '/login.html';
    }
}
fetchUserInfo();

btnSubmit.addEventListener('click', async () => {
    const text = rawInput.value.trim();
    if (!text) return;

    // Show loading state
    btnSubmit.disabled = true;
    loader.style.display = 'block';
    btnText.innerText = 'Analyzing...';

    try {
        const response = await fetch('/api/v1/status', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                rawContent: text
            })
        });

        if (!response.ok) throw new Error('Failed to parse status');

        const data = await response.json();
        updateUI(data);

    } catch (error) {
        console.error('Error:', error);
        alert('Could not connect to the backend. Is it running?');
    } finally {
        btnSubmit.disabled = false;
        loader.style.display = 'none';
        btnText.innerText = 'Parse & Save Status';
    }
});

function updateUI(data) {
    const report = data.formattedReport;

    // Clear lists
    yesterdayList.innerHTML = '';
    todayList.innerHTML = '';
    blockerList.innerHTML = '';

    // Update Yesterday
    if (report.yesterday.length === 0) {
        yesterdayList.innerHTML = '<li class="status-item">None identified</li>';
    } else {
        report.yesterday.forEach(item => {
            const li = document.createElement('li');
            li.className = 'status-item';
            li.innerText = item;
            yesterdayList.appendChild(li);
        });
    }

    // Update Today
    if (report.today.length === 0) {
        todayList.innerHTML = '<li class="status-item">None identified</li>';
    } else {
        report.today.forEach(item => {
            const li = document.createElement('li');
            li.className = 'status-item';
            li.innerText = item;
            todayList.appendChild(li);
        });
    }

    // Update Blockers
    if (report.blockers.length === 0) {
        blockerList.innerHTML = '<li class="status-item">None identified</li>';
    } else {
        report.blockers.forEach(item => {
            const li = document.createElement('li');
            li.className = 'status-item blocker';
            li.innerText = item;
            blockerList.appendChild(li);
        });
    }

    // Update Stats
    hoursSpent.innerText = `${report.hoursSpent}h total`;
    confidenceScore.innerText = `${Math.round(data.confidenceScore * 100)}%`;

    // Success Animation
    btnSubmit.style.background = 'var(--accent-success)';
    setTimeout(() => {
        btnSubmit.style.background = 'var(--primary)';
    }, 2000);
}

// Export functions
function downloadReport(format) {
    window.location.href = `/api/v1/status/export/${format}`;
}
