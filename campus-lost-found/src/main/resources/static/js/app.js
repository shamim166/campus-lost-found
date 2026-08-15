/**
 * Campus Lost & Found — Main JavaScript
 * app.js
 */

// ==============================================
// 1. Bootstrap Tooltip & Popover Initialization
// ==============================================
document.addEventListener('DOMContentLoaded', function () {

    // Initialize all Bootstrap tooltips
    const tooltipEls = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    tooltipEls.forEach(el => new bootstrap.Tooltip(el));

    // Auto-dismiss flash alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert.auto-dismiss');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // ==============================================
    // 2. Image Preview on Report Forms
    // ==============================================
    const imageInput = document.getElementById('imageFile');
    const imagePreview = document.getElementById('imagePreview');
    if (imageInput && imagePreview) {
        imageInput.addEventListener('change', function () {
            const file = this.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    imagePreview.src = e.target.result;
                    imagePreview.style.display = 'block';
                };
                reader.readAsDataURL(file);
            }
        });
    }

    // ==============================================
    // 3. Form Submit Loading State
    // ==============================================
    const forms = document.querySelectorAll('form[data-loading]');
    forms.forEach(function (form) {
        form.addEventListener('submit', function () {
            const submitBtn = form.querySelector('[type="submit"]');
            if (submitBtn) {
                const originalText = submitBtn.innerHTML;
                submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status"></span>Processing...';
                submitBtn.disabled = true;
            }
        });
    });

    // ==============================================
    // 4. Notification Badge Auto-Update
    // ==============================================
    const notifBadge = document.getElementById('notif-badge');
    if (notifBadge) {
        // Optionally poll /api/notifications/unread-count every 60 seconds
        setInterval(function () {
            fetch('/notifications/api/unread-count')
                .then(r => r.json())
                .then(data => {
                    if (data.count > 0) {
                        notifBadge.textContent = data.count;
                        notifBadge.style.display = 'inline';
                    } else {
                        notifBadge.style.display = 'none';
                    }
                })
                .catch(() => {}); // Silently ignore if not authenticated
        }, 60000);
    }

    // ==============================================
    // 5. Smooth scroll for anchor links
    // ==============================================
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                e.preventDefault();
                target.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        });
    });

    // ==============================================
    // 6. Mobile filter drawer toggle
    // ==============================================
    const filterToggleBtn = document.getElementById('filterToggle');
    const filterDrawer = document.getElementById('filterDrawer');
    if (filterToggleBtn && filterDrawer) {
        filterToggleBtn.addEventListener('click', function () {
            filterDrawer.classList.toggle('show');
        });
    }

});

// ==============================================
// 7. Confirm delete actions
// ==============================================
function confirmDelete(message) {
    return confirm(message || 'Are you sure you want to delete this item? This action cannot be undone.');
}
