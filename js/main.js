// Main JavaScript - Navigation and Utilities

// Utility Functions
function $(selector) {
  return document.querySelector(selector);
}

function $$(selector) {
  return document.querySelectorAll(selector);
}

// Navigation
function navigateTo(page) {
  window.location.href = page;
}

// Smooth Scroll
function smoothScroll(target) {
  const element = $(target);
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' });
  }
}

// Dashboard Tab Switching (if on dashboard page)
if (typeof switchTab === 'function') {
  // Tab switching is defined in dashboard.js
  const tabButtons = $$('[data-tab]');
  tabButtons.forEach(button => {
    button.addEventListener('click', () => {
      const tabName = button.getAttribute('data-tab');
      switchTab(tabName);
    });
  });
}

// Initialize tooltips or other global features
document.addEventListener('DOMContentLoaded', () => {
  console.log('UniConnect loaded successfully');

  // Add any global event listeners here
  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn && typeof handleLogout === 'function') {
    logoutBtn.addEventListener('click', handleLogout);
  }
});
