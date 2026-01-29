// Dashboard Integration - Connect to Backend API

// Check if user is authenticated
const isGuest = localStorage.getItem('uniconnect_guest') === '1';
if (!TokenManager.isAuthenticated() && !isGuest) {
    window.location.href = 'login.html';
}

// State
let currentTab = 'feed';
let currentUser = null;

// Initialize Dashboard
async function initDashboard() {
    try {
        LoadingManager.show('Loading dashboard...');

        // Load current user
        currentUser = TokenManager.getUser();
        updateUserDisplay();

        // Load initial tab content
        switchTab('feed');
        loadTopContributors();

        LoadingManager.hide();
    } catch (error) {
        LoadingManager.hide();
        Toast.error('Failed to load dashboard');
        console.error(error);
    }
}

// Update user display in header
function updateUserDisplay() {
    const userNameEl = document.getElementById('userName');
    const userRoleEl = document.getElementById('userRole');
    if (isGuest) {
        if (userNameEl) userNameEl.textContent = 'Guest User';
        if (userRoleEl) userRoleEl.textContent = 'GUEST';
        return;
    }
    if (userNameEl && currentUser) {
        userNameEl.textContent = currentUser.name;
    }
    if (userRoleEl && currentUser) {
        userRoleEl.textContent = currentUser.role;
    }
}

// Handle logout
function handleLogout() {
    if (confirm('Are you sure you want to logout?')) {
        localStorage.removeItem('uniconnect_guest');
        AuthAPI.logout();
    }
}

async function loadTopContributors() {
    try {
        const contributors = await PostAPI.getTopContributors();
        const container = document.getElementById('topContributorsList');
        if (!container) return;

        if (contributors.length === 0) {
            container.innerHTML = '<p class="text-xs text-muted">No contributors yet</p>';
            return;
        }

        container.innerHTML = contributors.map((user, index) => `
            <div class="flex items-center justify-between mb-2">
                <div class="flex items-center gap-2">
                    <div style="width:24px; height:24px; background:${['#bfdbfe', '#bbf7d0', '#fef08a', '#fed7aa', '#fecaca'][index % 5]}; border-radius:50%; display: flex; align-items: center; justify-content: center; font-size: 10px; font-weight: bold;">
                        ${user.name.charAt(0)}
                    </div>
                    <span class="text-sm font-medium">${escapeHtml(user.name)}</span>
                </div>
                <span class="text-xs text-muted">${user.postCount} posts</span>
            </div>
        `).join('');
    } catch (error) {
        console.error('Failed to load top contributors:', error);
    }
}

// Tab switching
function switchTab(tabName) {
    currentTab = tabName;

    // Update active tab UI
    const tabs = document.querySelectorAll('[data-tab]');
    tabs.forEach(tab => {
        tab.classList.toggle('active', tab.getAttribute('data-tab') === tabName);
    });

    // Update content sections visibility
    const sections = document.querySelectorAll('.content-section');
    sections.forEach(section => {
        if (section.id === tabName) {
            section.classList.remove('hidden');
        } else {
            section.classList.add('hidden');
        }
    });

    // Load tab content
    loadTabContent(tabName);
}

// Load content based on active tab
async function loadTabContent(tabName) {
    try {
        LoadingManager.show();

        switch (tabName) {
            case 'feed':
                await loadFeed();
                break;
            case 'pyq':
                await loadPYQs();
                break;
            case 'confessions':
                await loadConfessions();
                break;
            case 'alumni':
                await loadAlumni();
                break;
            case 'freshers':
                await loadFreshersHub();
                break;
            case 'marketplace':
                await loadMarketplace();
                break;
            case 'events':
                await loadEvents();
                break;
            case 'messages':
                await loadMessagesTab();
                break;
            case 'searchResults':
                // Handled specifically by performGlobalSearch
                break;
            default:
                console.warn('Unknown tab:', tabName);
        }

        LoadingManager.hide();
    } catch (error) {
        LoadingManager.hide();
        Toast.error(`Failed to load ${tabName}`);
        console.error(error);
    }
}

// === FRESHERS HUB ===
async function loadFreshersHub() {
    try {
        const [foodCourts, pgs, clubs] = await Promise.all([
            FoodCourtsAPI.getFoodCourts(),
            PGAPI.getPGs(),
            ClubsAPI.getClubs()
        ]);

        renderFoodCourts(foodCourts);
        renderPGs(pgs);
        renderClubs(clubs);
    } catch (error) {
        console.error('Failed to load Freshers Hub:', error);
    }
}

function renderFoodCourts(courts) {
    const container = document.getElementById('foodCourtsContainer');
    if (!container) return;

    container.innerHTML = courts.length ? courts.map(court => `
        <div class="flex justify-between items-center p-2 border-b">
            <div>
                <span class="font-bold">${escapeHtml(court.name)}</span>
                <p class="text-xs text-muted">${escapeHtml(court.location)}</p>
            </div>
            <span class="text-sm">⭐ ${court.rating || 'N/A'}</span>
        </div>
    `).join('') : '<p class="text-muted text-sm">No food courts listed</p>';
}

function renderPGs(pgs) {
    const container = document.getElementById('pgContainer');
    if (!container) return;

    container.innerHTML = pgs.length ? pgs.map(pg => `
        <div class="p-2 border-b">
            <div class="flex justify-between">
                <span class="font-bold">${escapeHtml(pg.name)}</span>
                <span class="text-primary font-bold">₹${pg.rent}/mo</span>
            </div>
            <p class="text-xs text-muted">${escapeHtml(pg.address)}</p>
        </div>
    `).join('') : '<p class="text-muted text-sm">No PGs listed</p>';
}

function renderClubs(clubs) {
    const container = document.getElementById('clubsContainer');
    if (!container) return;

    container.innerHTML = clubs.length ? clubs.map(club => `
        <div class="card flex flex-col items-center p-3 text-center">
            <div class="user-avatar mb-2">${club.name.charAt(0)}</div>
            <h4 class="font-bold text-sm text-truncate">${escapeHtml(club.name)}</h4>
            <button class="btn btn-sm btn-outline mt-2 text-xs" onclick="Toast.info('Join request sent!')">Join</button>
        </div>
    `).join('') : '<p class="text-muted text-sm">No clubs found</p>';
}

// Unified Modal Handlers
function openUnifiedModal(type = 'NORMAL') {
    if (isGuest) {
        Toast.info('Please log in to create a post.');
        return;
    }
    const selector = document.getElementById('createPostType');
    if (selector) {
        selector.value = type;
        toggleUnifiedFields();
    }
    Modal.open('createPostModal');
}

function toggleUnifiedFields() {
    const type = document.getElementById('createPostType').value;
    const general = document.getElementById('generalFields');
    const pyq = document.getElementById('pyqFields');
    const marketplace = document.getElementById('marketplaceFields');
    const modalTitle = document.getElementById('modalTitle');

    // Reset visibility
    general.classList.add('hidden');
    pyq.classList.add('hidden');
    marketplace.classList.add('hidden');

    if (type === 'NORMAL' || type === 'CONFESSION') {
        general.classList.remove('hidden');
        modalTitle.textContent = type === 'CONFESSION' ? 'Post Anonymously' : 'Create General Post';
    } else if (type === 'PYQ') {
        pyq.classList.remove('hidden');
        modalTitle.textContent = 'Upload PYQ';
    } else if (type === 'MARKETPLACE') {
        marketplace.classList.remove('hidden');
        modalTitle.textContent = 'List Item for Sale';
    }
}

async function handleUnifiedSubmit() {
    const type = document.getElementById('createPostType').value;

    if (type === 'NORMAL' || type === 'CONFESSION') {
        await handleCreatePost(type);
    } else if (type === 'PYQ') {
        await handlePyqUpload();
    } else if (type === 'MARKETPLACE') {
        await handleMarketplaceListing();
    }
}

// Keep existing logic but adapt for unification
async function handlePyqUpload() {
    const subject = document.getElementById('pyqSubject').value;
    const year = document.getElementById('pyqYear').value;
    const type = document.getElementById('pyqType').value;
    const file = document.getElementById('pyqFile').files[0];

    if (!subject || !year || !file) {
        Toast.error('Please fill all required fields');
        return;
    }

    const formData = new FormData();
    formData.append('subject', subject);
    formData.append('year', year);
    formData.append('examType', type);
    formData.append('file', file);

    try {
        LoadingManager.show('Uploading PYQ...');
        await PYQAPI.uploadPYQ(formData);
        Modal.close('createPostModal');

        // Switch to PYQ tab to see the new upload
        switchTab('pyq');

        Toast.success('PYQ uploaded successfully!');
    } catch (error) {
        console.error('PYQ Upload Error:', error);
        Toast.error(error.message || 'Failed to upload PYQ');
    } finally {
        LoadingManager.hide();
    }
}

async function handleMarketplaceListing() {
    const title = document.getElementById('itemTitle').value;
    const description = document.getElementById('itemDescription').value;
    const price = document.getElementById('itemPrice').value;
    const category = document.getElementById('itemCategory').value;

    if (!title || !price) {
        Toast.error('Please enter title and price');
        return;
    }

    try {
        LoadingManager.show('Listing item...');
        await MarketplaceAPI.createListing({ title, description, price, category });
        Modal.close('createPostModal');

        // Switch to Marketplace tab
        switchTab('marketplace');

        Toast.success('Item listed successfully!');
    } catch (error) {
        Toast.error(error.message);
    } finally {
        LoadingManager.hide();
    }
}

// === FEED ===
async function loadFeed() {
    const posts = await PostsAPI.getPosts('NORMAL');
    renderPosts(posts, 'feedContainer');
}

function renderPosts(data, containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;

    // Handle Spring Data Page object or direct array
    const posts = Array.isArray(data) ? data : (data.content || []);

    container.innerHTML = posts.length ? posts.map(post => createPostHTML(post)).join('') :
        '<p class="empty-state">No posts yet. Be the first to post!</p>';
}

function createPostHTML(post) {
    const currentUser = TokenManager.getUser();
    const canDelete = currentUser && post.user && currentUser.userId === post.user.id;
    const isAnon = post.isAnonymous || post.anonymous;

    return `
        <div class="post-card card" id="post-${post.id}">
            <div class="post-header">
                <div class="post-user">
                    <div class="user-avatar">${isAnon ? '🎭' : post.user?.name?.charAt(0) || '?'}</div>
                    <div>
                        <div class="user-name">${isAnon ? 'Anonymous' : escapeHtml(post.user?.name || 'Unknown')}</div>
                        <div class="post-time text-muted text-sm">${formatDate(post.createdAt)}</div>
                    </div>
                </div>
                ${canDelete ? `
                <button class="btn-icon text-muted" onclick="deletePost(${post.id})" title="Delete Post">
                    <small>🗑️</small>
                </button>` : ''}
            </div>
            <div class="post-content">${escapeHtml(post.content)}</div>
            <div class="post-footer">
                <button class="btn btn-outline btn-sm" onclick="handleLike(${post.id})">
                    ❤️ ${post.likeCount || 0}
                </button>
                <button class="btn btn-outline btn-sm" onclick="toggleComments(${post.id})">
                    💬 ${post.comments?.length || 0}
                </button>
            </div>
            <div id="comments-${post.id}" class="comments-section" style="display: none;">
                ${renderComments(post.comments || [], isAnon)}
                <div class="comment-input">
                    <input type="text" placeholder="Write a comment..." id="comment-input-${post.id}">
                    <button class="btn btn-primary btn-sm" onclick="handleComment(${post.id})">Post</button>
                </div>
            </div>
        </div>
    `;
}

function renderComments(comments, forceAnonymous = false) {
    if (!comments.length) return '<p class="text-muted text-sm">No comments yet</p>';
    const currentUser = TokenManager.getUser();

    return comments.map(comment => {
        const canDelete = currentUser && comment.user && currentUser.userId === comment.user.id;
        const isAnon = forceAnonymous || comment.isAnonymous || comment.anonymous;

        return `
            <div class="comment">
                <div class="comment-header" style="display: flex; justify-content: space-between; align-items: center;">
                    <strong>${isAnon ? 'Anonymous' : escapeHtml(comment.user?.name || 'Unknown')}</strong>
                    <div style="display: flex; align-items: center; gap: 8px;">
                        <span class="text-muted text-sm">${formatDate(comment.createdAt)}</span>
                        ${canDelete ? `
                        <button class="btn-icon text-muted" onclick="deleteComment(${comment.id})" title="Delete Comment" style="padding: 0; line-height: 1;">
                            <small>🗑️</small>
                        </button>` : ''}
                    </div>
                </div>
                <p>${escapeHtml(comment.content)}</p>
            </div>
        `;
    }).join('');
}

async function handleLike(postId) {
    try {
        await PostsAPI.likePost(postId);
        await loadTabContent(currentTab);
        Toast.success('Post liked!');
    } catch (error) {
        Toast.error('Failed to like post');
    }
}

function toggleComments(postId) {
    const commentsEl = document.getElementById(`comments-${postId}`);
    if (commentsEl) {
        commentsEl.style.display = commentsEl.style.display === 'none' ? 'block' : 'none';
    }
}

async function handleComment(postId) {
    const input = document.getElementById(`comment-input-${postId}`);
    const content = input?.value?.trim();

    if (!content) return;

    try {
        await PostsAPI.addComment(postId, content);
        input.value = '';
        await loadTabContent(currentTab);
        Toast.success('Comment posted!');
    } catch (error) {
        Toast.error('Failed to post comment');
    }
}

async function deleteComment(commentId) {
    if (!confirm('Are you sure you want to delete this comment?')) return;

    try {
        await PostsAPI.deleteComment(commentId);
        await loadTabContent(currentTab);
        Toast.success('Comment deleted');
    } catch (error) {
        Toast.error('Failed to delete comment');
    }
}

// Create Post
async function handleCreatePost(type) {
    const content = document.getElementById('postContent')?.value?.trim();
    const isAnonymous = type === 'CONFESSION';

    if (!content) {
        Toast.error('Please enter post content');
        return;
    }

    try {
        LoadingManager.show('Creating post...');
        await PostsAPI.createPost(content, isAnonymous ? 'CONFESSION' : 'NORMAL', isAnonymous);

        Modal.close('createPostModal');
        document.getElementById('postContent').value = '';

        // Switch to relevant tab
        switchTab(type === 'CONFESSION' ? 'confessions' : 'feed');

        Toast.success('Post created successfully!');
    } catch (error) {
        Toast.error('Failed to create post');
    } finally {
        LoadingManager.hide();
    }
}

// === PYQs ===
async function loadPYQs() {
    const pyqs = await PYQAPI.getPYQs();
    const container = document.getElementById('pyqContainer');
    if (!container) return;

    const currentUser = TokenManager.getUser();

    container.innerHTML = pyqs.length ? pyqs.map(pyq => {
        const canDelete = currentUser && pyq.uploadedBy && currentUser.userId === pyq.uploadedBy.id;
        return `
            <div class="pyq-card card" id="pyq-${pyq.id}">
                <div class="flex justify-between">
                    <h3>${escapeHtml(pyq.subject)}</h3>
                    ${canDelete ? `<button onclick="deletePYQ(${pyq.id})" class="text-muted"><small>🗑️</small></button>` : ''}
                </div>
                <p class="text-muted">Year: ${pyq.year} | ${pyq.examType || 'Exam'}</p>
                <p class="text-sm">Uploaded by: ${escapeHtml(pyq.uploadedBy?.name || 'Unknown')}</p>
                <a href="${pyq.fileUrl}" target="_blank" class="btn btn-primary btn-sm">📄 Download PDF</a>
            </div>
        `;
    }).join('') : '<p class="empty-state">No PYQs available yet</p>';
}

// === CONFESSIONS ===
async function loadConfessions() {
    const posts = await PostsAPI.getPosts('CONFESSION');
    renderPosts(posts, 'confessionsContainer');
}

// === ALUMNI ===
async function loadAlumni() {
    const alumni = await AlumniAPI.getProfiles();
    const container = document.getElementById('alumniContainer');
    if (!container) return;

    container.innerHTML = alumni.length ? alumni.map(profile => `
        <div class="alumni-card card">
            <h3>${escapeHtml(profile.user?.name || 'Alumni')}</h3>
            <p><strong>${escapeHtml(profile.jobRole)}</strong> at ${escapeHtml(profile.company)}</p>
            <p class="text-muted">${profile.yearsOfExperience} years experience</p>
            ${profile.review ? `<p class="text-sm">"${escapeHtml(profile.review)}"</p>` : ''}
            ${profile.linkedinUrl ? `<a href="${profile.linkedinUrl}" target="_blank" class="btn btn-outline btn-sm">LinkedIn</a>` : ''}
        </div>
    `).join('') : '<p class="empty-state">No alumni profiles yet</p>';
}

// === MARKETPLACE ===
async function loadMarketplace() {
    const items = await MarketplaceAPI.getItems();
    const container = document.getElementById('marketplaceContainer');
    if (!container) return;

    const currentUser = TokenManager.getUser();

    container.innerHTML = items.length ? items.map(item => {
        const isSeller = currentUser && item.seller && currentUser.userId === item.seller.id;
        return `
            <div class="marketplace-card card" id="item-${item.id}">
                <div class="flex justify-between">
                    <h3>${escapeHtml(item.title)}</h3>
                    ${isSeller ? `<button onclick="deleteMarketplaceItem(${item.id})" class="text-muted"><small>🗑️</small></button>` : ''}
                </div>
                ${item.imageUrl ? `<img src="${item.imageUrl}" alt="Item">` : ''}
                <p class="text-lg font-bold">₹${item.price}</p>
                <p class="text-sm">${escapeHtml(item.description)}</p>
                <p class="text-muted text-sm">Category: ${item.category}</p>
                <p class="text-sm">Seller: ${escapeHtml(item.seller?.name || 'Unknown')}</p>
                ${!isSeller ?
                `<button class="btn btn-primary btn-sm w-full mt-2" onclick="openChatWithSeller(${item.seller?.id}, ${item.id}, '${escapeHtml(item.title)}')">Buy / Contact Seller</button>` :
                `<button class="btn btn-outline btn-sm w-full mt-2" onclick="switchTab('messages')">View Active Chats</button>`
            }
            </div>
        `;
    }).join('') : '<p class="empty-state">No items for sale</p>';
}

// === MESSAGES / CHAT ===
let activeChatUserId = null;

async function openChatWithSeller(sellerId, itemId, itemTitle) {
    if (!sellerId) return;
    activeChatUserId = sellerId;
    switchTab('messages');
    await loadConversation(sellerId);

    // Auto-fill a message if it's from a specific item
    const input = document.getElementById('chatMessageInput');
    if (input) {
        input.value = `Hi, I'm interested in buying "${itemTitle}". Is it still available?`;
    }
}

async function loadMessagesTab() {
    try {
        const partners = await ChatAPI.getChatPartners();
        const partnersList = document.getElementById('chatPartnersList');
        if (!partnersList) return;

        partnersList.innerHTML = partners.map(user => `
            <div class="chat-partner-item ${user.id === activeChatUserId ? 'active' : ''}" 
                 onclick="loadConversation(${user.id})"
                 style="padding: 1rem; border-bottom: 1px solid var(--border-color); cursor: pointer; transition: background 0.2s;">
                <strong>${escapeHtml(user.name)}</strong>
                <p class="text-xs text-muted">Click to view chat</p>
            </div>
        `).join('');

        if (partners.length === 0) {
            partnersList.innerHTML = '<p class="text-muted text-center p-4">No conversations yet</p>';
        }
    } catch (error) {
        console.error('Failed to load chat partners:', error);
    }
}

async function loadConversation(otherUserId) {
    activeChatUserId = otherUserId;

    // Highlight active partner in list
    const items = document.querySelectorAll('.chat-partner-item');
    items.forEach(item => {
        item.classList.remove('active');
        // This is a bit hacky, normally we'd re-render or use a cleaner class toggle
    });

    const historyEl = document.getElementById('chatHistory');
    const inputArea = document.getElementById('chatInputArea');
    if (!historyEl || !inputArea) return;

    try {
        LoadingManager.show('Loading chat...');
        const messages = await ChatAPI.getConversation(otherUserId);
        const currentUser = TokenManager.getUser();

        // Find partner name from the sidebar list (since we don't have a separate user fetch yet)
        const partners = await ChatAPI.getChatPartners();
        const partner = partners.find(p => p.id === otherUserId);
        const partnerName = partner ? partner.name : 'User';

        const msgHtml = messages.length ? messages.map(msg => {
            const isMe = currentUser && (msg.sender.id === currentUser.userId || msg.sender.userId === currentUser.userId);
            return `
                <div class="message-wrapper" style="display: flex; justify-content: ${isMe ? 'flex-end' : 'flex-start'}; margin-bottom: 12px;">
                    <div class="message-bubble" style="background: ${isMe ? 'var(--primary-color)' : 'var(--card-bg)'}; 
                                                      color: ${isMe ? 'white' : 'inherit'}; 
                                                      padding: 8px 12px; border-radius: 12px; max-width: 70%;
                                                      border: ${isMe ? 'none' : '1px solid var(--border-color)'}">
                        <p style="margin: 0;">${escapeHtml(msg.content)}</p>
                        <span class="text-xs" style="opacity: 0.7; display: block; text-align: right; margin-top: 4px;">${formatDate(msg.createdAt)}</span>
                    </div>
                </div>
            `;
        }).join('') : '<p class="text-muted text-center" style="margin-top: 50px;">No messages yet. Say hello!</p>';

        historyEl.innerHTML = `
            <div class="chat-header" style="padding-bottom: 12px; border-bottom: 1px solid var(--border-color); margin-bottom: 16px;">
                <h3 class="text-lg font-bold">Chat with ${escapeHtml(partnerName)}</h3>
            </div>
            ${msgHtml}
        `;

        inputArea.classList.remove('hidden');
        setTimeout(() => { historyEl.scrollTop = historyEl.scrollHeight; }, 100);
    } catch (error) {
        console.error('Chat error:', error);
        Toast.error('Failed to load conversation');
    } finally {
        LoadingManager.hide();
    }
}

// Initial set up for chat send button
document.addEventListener('DOMContentLoaded', () => {
    const sendBtn = document.getElementById('sendMessageBtn');
    if (sendBtn) {
        sendBtn.onclick = async () => {
            const input = document.getElementById('chatMessageInput');
            const content = input?.value?.trim();
            if (!content || !activeChatUserId) return;

            try {
                await ChatAPI.sendMessage(activeChatUserId, content);
                input.value = '';
                await loadConversation(activeChatUserId);
            } catch (error) {
                Toast.error('Failed to send message');
            }
        };
    }
});

// === DELETE HANDLERS ===
async function deletePost(id) {
    if (!confirm('Are you sure you want to delete this post?')) return;
    try {
        await PostsAPI.deletePost(id);
        Toast.success('Post deleted');
        loadTabContent(currentTab);
    } catch (error) {
        Toast.error('Failed to delete');
    }
}

async function deletePYQ(id) {
    if (!confirm('Are you sure you want to delete this upload?')) return;
    try {
        await PYQAPI.deletePYQ(id);
        Toast.success('Upload deleted');
        loadPYQs();
    } catch (error) {
        Toast.error('Failed to delete');
    }
}

async function deleteMarketplaceItem(id) {
    if (!confirm('Are you sure you want to delete this listing?')) return;
    try {
        await MarketplaceAPI.deleteItem(id);
        Toast.success('Item deleted');
        loadMarketplace();
    } catch (error) {
        Toast.error('Failed to delete');
    }
}

// === EVENTS ===
async function loadEvents() {
    const events = await EventsAPI.getEvents();
    const container = document.getElementById('eventsContainer');
    if (!container) return;

    container.innerHTML = events.length ? events.map(event => `
        <div class="event-card card">
            ${event.posterUrl ? `<img src="${event.posterUrl}" alt="Event poster">` : ''}
            <h3>${escapeHtml(event.title)}</h3>
            <p class="text-muted">📅 ${formatDate(event.eventDate)}</p>
            <p class="text-muted">📍 ${escapeHtml(event.venue || 'TBA')}</p>
            <p>${escapeHtml(event.description)}</p>
        </div>
    `).join('') : '<p class="empty-state">No upcoming events</p>';
}

// === SEARCH ===
async function performGlobalSearch(query) {
    if (!query) return;

    switchTab('searchResults');
    const containerPosts = document.getElementById('searchPostsContainer');
    const containerMarket = document.getElementById('searchMarketplaceContainer');

    if (!containerPosts || !containerMarket) return;

    containerPosts.innerHTML = '<p class="text-muted">Searching posts...</p>';
    containerMarket.innerHTML = '<p class="text-muted">Searching marketplace...</p>';

    try {
        const results = await SearchAPI.search(query);

        // Render Posts
        if (results.posts && results.posts.length) {
            containerPosts.innerHTML = '<h3 class="font-bold mb-2">Posts</h3>' +
                results.posts.map(post => createPostHTML(post)).join('');
        } else {
            containerPosts.innerHTML = '<p class="text-muted">No posts found matching "' + escapeHtml(query) + '"</p>';
        }

        // Render Marketplace
        if (results.marketplace && results.marketplace.length) {
            const currentUser = TokenManager.getUser();
            containerMarket.innerHTML = '<h3 class="font-bold mb-4 w-full" style="grid-column: 1/-1; margin-top: 2rem;">Marketplace Items</h3>' +
                results.marketplace.map(item => {
                    const isSeller = currentUser && item.seller && currentUser.userId === item.seller.id;
                    return `
                        <div class="marketplace-card card" id="item-${item.id}">
                            <div class="flex justify-between">
                                <h3>${escapeHtml(item.title)}</h3>
                                ${isSeller ? `<button onclick="deleteMarketplaceItem(${item.id})" class="text-muted"><small>🗑️</small></button>` : ''}
                            </div>
                            ${item.imageUrl ? `<img src="${item.imageUrl}" alt="Item">` : ''}
                            <p class="text-lg font-bold">₹${item.price}</p>
                            <p class="text-sm">${escapeHtml(item.description)}</p>
                            <p class="text-muted text-sm">Category: ${item.category}</p>
                            <p class="text-sm">Seller: ${escapeHtml(item.seller?.name || 'Unknown')}</p>
                            ${!isSeller ?
                            `<button class="btn btn-primary btn-sm w-full mt-2" onclick="openChatWithSeller(${item.seller?.id}, ${item.id}, '${escapeHtml(item.title)}')">Buy / Contact Seller</button>` :
                            `<button class="btn btn-outline btn-sm w-full mt-2" onclick="switchTab('messages')">View Active Chats</button>`
                        }
                        </div>
                    `;
                }).join('');
        } else {
            containerMarket.innerHTML = '<p class="text-muted w-full" style="grid-column: 1/-1">No items found matching "' + escapeHtml(query) + '"</p>';
        }

    } catch (error) {
        console.error('Search failed:', error);
        Toast.error('Search failed');
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    initDashboard();

    // Hook up search input
    const searchInput = document.getElementById('globalSearch');
    if (searchInput) {
        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                performGlobalSearch(searchInput.value.trim());
            }
        });
    }

    // Auto-refresh chat every 5 seconds if on messages tab
    setInterval(() => {
        if (currentTab === 'messages') {
            loadMessagesTab();
            if (activeChatUserId) {
                // We avoid LoadingManager for background refreshes
                ChatAPI.getConversation(activeChatUserId).then(messages => {
                    const historyEl = document.getElementById('chatHistory');
                    if (historyEl) {
                        // Check if we have new messages
                        const currentMsgs = historyEl.querySelectorAll('.message-wrapper').length;
                        if (messages.length > currentMsgs) {
                            // Update just the message area (silently)
                            const currentUser = TokenManager.getUser();
                            const partnerName = document.querySelector('.chat-partner-item.active strong')?.textContent || 'User';

                            const msgHtml = messages.map(msg => {
                                const isMe = currentUser && (msg.sender.id === currentUser.userId || msg.sender.userId === currentUser.userId);
                                return `
                                    <div class="message-wrapper" style="display: flex; justify-content: ${isMe ? 'flex-end' : 'flex-start'}; margin-bottom: 12px;">
                                        <div class="message-bubble" style="background: ${isMe ? 'var(--primary-color)' : 'var(--card-bg)'}; 
                                                                          color: ${isMe ? 'white' : 'inherit'}; 
                                                                          padding: 8px 12px; border-radius: 12px; max-width: 70%;
                                                                          border: ${isMe ? 'none' : '1px solid var(--border-color)'}">
                                            <p style="margin: 0;">${escapeHtml(msg.content)}</p>
                                            <span class="text-xs" style="opacity: 0.7; display: block; text-align: right; margin-top: 4px;">${formatDate(msg.createdAt)}</span>
                                        </div>
                                    </div>
                                `;
                            }).join('');

                            historyEl.innerHTML = `
                                <div class="chat-header" style="padding-bottom: 12px; border-bottom: 1px solid var(--border-color); margin-bottom: 16px;">
                                    <h3 class="text-lg font-bold">Chat with ${escapeHtml(partnerName)}</h3>
                                </div>
                                ${msgHtml}
                            `;
                            historyEl.scrollTop = historyEl.scrollHeight;
                        }
                    }
                }).catch(e => console.warn('bg refresh fail'));
            }
        }
    }, 5000);
});
