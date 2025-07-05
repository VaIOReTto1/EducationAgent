/**
 * =================================================================================
 * V2.0 SCRIPT
 * Author: Gemini Advanced
 * Features: Rich mock data, modular logic, data visualizations, full interactivity.
 * =================================================================================
 */
document.addEventListener('DOMContentLoaded', () => {

    /**
     * =================================================================================
     * MOCK DATA
     * =================================================================================
     */
    const mockData = {
      overview: {
        stats: [
            { id: 'active-students', label: '活跃学生数', value: '218', icon: 'bi-person-video3', color: 'primary' },
            { id: 'pending-tasks', label: '待批改作业', value: '32', icon: 'bi-file-earmark-check', color: 'warning' },
            { id: 'system-health', label: 'AI 助教在线率', value: '99.9%', icon: 'bi-robot', color: 'success' },
            { id: 'kb-articles', label: '知识库文档', value: '412', icon: 'bi-collection-fill', color: 'info' }
        ],
        notifications: [
          { text: '王五的《高等数学》期末考试申请已提交', time: '15分钟前', icon: 'bi-file-text-fill text-primary' },
          { text: 'AI助教成功解答了李四关于“矩阵特征值”的疑问', time: '1小时前', icon: 'bi-lightbulb-fill text-success' },
          { text: '“C++程序设计”课程的第5章作业将于3小时后截止', time: '2小时前', icon: 'bi-clock-history text-warning' },
          { text: '系统安全更新已完成，无任何风险', time: '昨天', icon: 'bi-shield-check-fill text-info' },
          { text: '您上传的文档“傅里叶变换详解”已被引用12次', time: '昨天', icon: 'bi-bookmark-star-fill text-secondary' },
          { text: '检测到学生“赵六”的学习模式可能存在困难', time: '2天前', icon: 'bi-exclamation-triangle-fill text-danger' }
        ]
      },
      students: {
        progress: Array.from({ length: 30 }, (_, i) => ({
          id: `S${(i + 1).toString().padStart(3, '0')}`,
          name: ['张三','李四','王五','赵六','孙七','周八','吴九','郑十'][i % 8],
          course: ['高等数学','线性代数','大学物理','C++程序设计','数据结构','操作系统'][i % 6],
          progress: Math.floor(Math.random() * 80) + 20,
          questions: Math.floor(Math.random() * 10),
          last_active: `2024-05-${Math.floor(Math.random()*10)+10} ${Math.floor(Math.random()*24)}:${Math.floor(Math.random()*60).toString().padStart(2,'0')}`
        })),
        efficiency: [
          { subject: '数学', value: 85 }, { subject: '编程', value: 92 },
          { subject: '物理', value: 70 }, { subject: '数据结构', value: 78 },
          { subject: '英语', value: 65 }
        ],
        knowledgeMastery: [
            { topic: '极限', mastery: 95 }, { topic: '导数', mastery: 90 }, { topic: '微分中值定理', mastery: 82 },
            { topic: '不定积分', mastery: 75 }, { topic: '定积分', mastery: 88 }, { topic: '矩阵', mastery: 70 },
            { topic: '向量空间', mastery: 65 }, { topic: '特征值', mastery: 78 }, { topic: '牛顿定律', mastery: 92 },
            { topic: '虚功原理', mastery: 60 }, { topic: '指针', mastery: 98 }, { topic: '递归', mastery: 85 },
            { topic: '动态规划', mastery: 77 }, { topic: 'TCP/IP', mastery: 89 }, { topic: 'HTTP/3', mastery: 68 }
        ]
      },
      knowledgeBase: Array.from({ length: 30 }, (_, i) => ({
        id: `KB${(i + 1).toString().padStart(3, '0')}`,
        title: ['第一章：函数、极限与连续', '第二章：导数与微分', '附录：常用积分公式表', '线性代数的核心思想', '如何求解矩阵的特征值', 'C++模板元编程入门', '操作系统-进程管理'][i % 7],
        author: ['王老师', '李老师', '张老师', 'AI助教'][i % 4],
        created_at: `2024-0${Math.floor(i/6)+1}-${Math.floor(i%28)+1}`,
        views: Math.floor(Math.random() * 3000),
        tags: [['核心', '基础'], ['核心', '难点'], ['工具', '速查'], ['思想', '入门'], ['技巧', '计算'], ['进阶', '编程'], ['核心', '系统']][i % 7]
      })),
      messages: Array.from({ length: 15 }, (_, i) => ({
        id: `MSG${i + 1}`,
        sender: ['李四', '系统通知', '王五', '助教小A', '赵六', '张三'][i % 6],
        subject: ['关于定积分计算的问题', '你的课程报告已生成', '作业提交确认', '知识库文档勘误', '请假申请', '关于期末项目的疑问'][i % 6],
        preview: '老师，我在计算这个定积分时遇到了困难，特别是关于分部积分法的部分，能否请您... ',
        content: `<h3>关于定积分计算的问题</h3><p>尊敬的王老师：</p><p>您好！</p><p>我在学习《高等数学》第七章“定积分”时，遇到了一些困难，尤其是在处理以下类型的题目时：</p><pre><code>∫(x^2 * e^x) dx</code></pre><p>我尝试使用分部积分法，设 u = x^2, dv = e^x dx，但计算过程非常繁琐，且容易出错。想请教一下是否有更简洁的思路，或者在运用分部积分法时有什么需要注意的技巧？</p><p>另外，我对教材中提到的“表格积分法”不太理解，希望老师能提供一些相关的学习资料或例子。</p><p>感谢您的指导！</p><p>学生：李四</p><p>${new Date().toLocaleString()}<br /><br /><br /></p>`,
        unread: Math.random() > 0.5,
        time: i % 3 === 0 ? '10:45' : (i % 3 === 1 ? '昨天' : '2024-05-18')
      })),
      assignments: [
          { id: 'AS01', course: '高等数学', title: '第五章作业 - 积分应用', due: '2024-06-10', submitted: 48, total: 50},
          { id: 'AS02', course: 'C++程序设计', title: '项目一 - 学生管理系统', due: '2024-06-12', submitted: 45, total: 45},
          { id: 'AS03', course: '大学物理', title: '实验报告 - 光的干涉', due: '2024-06-15', submitted: 49, total: 50},
      ],
      exams: [
          { id: 'EX01', course: '高等数学', title: '期中考试', date: '2024-06-20 09:00', duration: '120分钟', status: '已结束'},
          { id: 'EX02', course: '线性代数', title: '期末考试', date: '2024-07-05 14:00', duration: '120分钟', status: '待开始'},
          { id: 'EX03', course: 'C++程序设计', title: '期末机考', date: '2024-07-08 10:00', duration: '150分钟', status: '待开始'},
      ]
    };
  
    /**
     * =================================================================================
     * DOM ELEMENTS & STATE
     * =================================================================================
     */
    const DOM = {
      body: document.body,
      themeSwitch: document.getElementById('theme-switch'),
      navLinks: document.querySelectorAll('.sidebar .nav-link'),
      contentSections: document.querySelectorAll('.content-section'),
      mobileNavToggle: document.getElementById('mobile-nav-toggle'),
      sidebar: document.querySelector('.sidebar'),
      addDocModal: new bootstrap.Modal(document.getElementById('add-doc-modal')),
      addDocForm: document.getElementById('add-doc-form'),
      addDocBtn: document.getElementById('add-doc-btn'),
      toastContainer: document.querySelector('.toast-container'),
      exportDataBtn: document.getElementById('export-data-btn'),
      clearCacheBtn: document.getElementById('clear-cache-btn'),
      messageListContainer: document.getElementById('message-list-container'),
      messageDetailContainer: document.getElementById('message-detail-container'),
      backToListBtn: document.getElementById('back-to-list-btn'),
    };
  
    /**
     * =================================================================================
     * UTILS & HELPERS
     * =================================================================================
     */
    const showToast = (message, type = 'info') => {
      const toastEl = document.createElement('div');
      toastEl.className = `toast align-items-center text-white bg-${type} border-0`;
      toastEl.setAttribute('role', 'alert');
      toastEl.setAttribute('aria-live', 'assertive');
      toastEl.setAttribute('aria-atomic', 'true');
      toastEl.innerHTML = `
        <div class="d-flex">
          <div class="toast-body">${message}</div>
          <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
      `;
      DOM.toastContainer.appendChild(toastEl);
      const toast = new bootstrap.Toast(toastEl, { delay: 3000 });
      toast.show();
      toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
    };
  
  
    /**
     * =================================================================================
     * RENDER FUNCTIONS
     * =================================================================================
     */
    const renderers = {
      overview() {
        // Stats Cards
        const statsContainer = document.getElementById('overview-stats');
        if (statsContainer) statsContainer.innerHTML = mockData.overview.stats.map(stat => `
          <div class="col-md-6 col-lg-3 mb-4">
            <div class="card stat-card">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <div class="stat-value">${stat.value}</div>
                    <div class="stat-label text-secondary">${stat.label}</div>
                  </div>
                  <i class="bi ${stat.icon} stat-icon text-${stat.color}"></i>
                </div>
              </div>
            </div>
          </div>
        `).join('');
  
        // Learning Activity Chart (Bar + Line)
        const activityChartEl = document.getElementById('learning-activity-chart');
        if (activityChartEl) {
          const activity = mockData.overview.activity;
          const maxBarValue = Math.max(...activity.map(a => a.value));
          const maxLineValue = Math.max(...activity.map(a => a.trend));
  
          const barPoints = activity.map(item => ((item.value / maxBarValue) * 100).toFixed(2));
          const linePoints = activity.map((item, i) => {
              const x = (i / (activity.length - 1)) * 100;
              const y = 100 - (item.trend / maxLineValue) * 100;
              return `${x.toFixed(2)},${y.toFixed(2)}`;
          }).join(' ');
  
          activityChartEl.innerHTML = `
            <div class="chart-container">
              ${activity.map((item, i) => `
                <div class="chart-bar-group">
                  <div class="chart-bar" style="height: ${barPoints[i]}%;"></div>
                  <div class="chart-label">${item.day}</div>
                </div>
              `).join('')}
              <svg class="chart-line-overlay" viewBox="0 0 100 100" preserveAspectRatio="none">
                  <polyline points="${linePoints}"></polyline>
              </svg>
            </div>`;
        }
  
        // Notifications
        const notificationsList = document.getElementById('latest-notifications');
        if(notificationsList) notificationsList.innerHTML = mockData.overview.notifications.map(n => `
          <li class="list-group-item d-flex align-items-center">
              <i class="${n.icon} me-3 fs-4"></i>
              <div class="flex-grow-1">
                  <div>${n.text}</div>
                  <small class="text-secondary">${n.time}</small>
              </div>
          </li>
        `).join('');
      },
  
      studentStats() {
          const progressContainer = document.getElementById('student-progress-table-container');
          if (progressContainer) progressContainer.innerHTML = `
              <table class="table table-hover align-middle">
                  <thead><tr><th>学生ID</th><th>姓名</th><th>学习课程</th><th>进度</th><th>问题数</th></tr></thead>
                  <tbody>
                      ${mockData.students.progress.map(s => `
                          <tr>
                              <td><span class="badge text-bg-secondary">${s.id}</span></td>
                              <td>${s.name}</td>
                              <td>${s.course}</td>
                              <td>
                                  <div class="progress" style="height: 20px;" title="${s.progress}%">
                                      <div class="progress-bar" role="progressbar" style="width: ${s.progress}%;"></div>
                                  </div>
                              </td>
                              <td><span class="badge rounded-pill text-bg-danger">${s.questions}</span></td>
                          </tr>
                      `).join('')}
                  </tbody>
              </table>`;
          
          // Radar Chart
          const radarChartEl = document.getElementById('class-efficiency-chart');
          if (radarChartEl) {
              const data = mockData.students.efficiency;
              const size = 200;
              const center = size / 2;
              const numAxes = data.length;
              const angleSlice = (Math.PI * 2) / numAxes;
  
              const getPoint = (value, index) => {
                  const angle = angleSlice * index - Math.PI / 2;
                  const x = center + (center * 0.8 * value / 100) * Math.cos(angle);
                  const y = center + (center * 0.8 * value / 100) * Math.sin(angle);
                  return `${x},${y}`;
              };
              
              const points = data.map((d, i) => getPoint(d.value, i)).join(' ');
              const axes = data.map((d, i) => `<line class="axis" x1="${center}" y1="${center}" x2="${getPoint(100, i).split(',')[0]}" y2="${getPoint(100, i).split(',')[1]}"></line>`).join('');
              const labels = data.map((d, i) => `<text class="label" x="${getPoint(110, i).split(',')[0]}" y="${getPoint(110, i).split(',')[1]}" text-anchor="middle" dominant-baseline="middle">${d.subject}</text>`).join('');
  
              radarChartEl.innerHTML = `
                  <svg class="radar-chart-svg" width="${size}" height="${size}" viewBox="0 0 ${size} ${size}">
                      ${axes}
                      <polygon class="data-polygon" points="${points}"></polygon>
                      ${labels}
                  </svg>`;
          }
  
          // Word Cloud
          const masteryContainer = document.getElementById('knowledge-mastery-container');
          if (masteryContainer) masteryContainer.innerHTML = mockData.students.knowledgeMastery
            .sort(() => 0.5 - Math.random())
            .map(item => {
              const size = 0.8 + (item.mastery - 60) / 40 * 1.2;
              const opacity = 0.6 + (item.mastery - 60) / 40 * 0.4;
              return `<span class="word-cloud-item" style="font-size: ${size.toFixed(2)}rem; opacity: ${opacity.toFixed(2)};">${item.topic}</span>`;
          }).join('');
      },
      
      knowledgeBase() {
        const kbContainer = document.getElementById('kb-table-container');
        if (!kbContainer) return;
        kbContainer.innerHTML = `
          <table class="table table-hover">
            <thead><tr><th>文档标题</th><th>创建者</th><th>查阅次数</th><th>标签</th><th>操作</th></tr></thead>
            <tbody>
              ${mockData.knowledgeBase.map(doc => `
                <tr>
                  <td><i class="bi bi-file-text-fill text-info"></i> ${doc.title}</td>
                  <td>${doc.author}</td>
                  <td>${doc.views}</td>
                  <td>${doc.tags.map(tag => `<span class="badge text-bg-light">${tag}</span>`).join(' ')}</td>
                  <td>
                    <button class="btn btn-sm btn-outline-primary edit-btn" aria-label="编辑"><i class="bi bi-pencil-square"></i></button>
                    <button class="btn btn-sm btn-outline-danger delete-btn" aria-label="删除"><i class="bi bi-trash-fill"></i></button>
                  </td>
                </tr>`).join('')}
            </tbody>
          </table>`;
      },
  
      assignments() {
        const container = document.getElementById('assignments-container');
        if(!container) return;
        container.innerHTML = mockData.assignments.map(a => `
          <div class="col-md-6 col-lg-4 mb-4">
            <div class="card h-100">
              <div class="card-body d-flex flex-column">
                <h5 class="card-title">${a.title}</h5>
                <h6 class="card-subtitle mb-2 text-secondary">${a.course}</h6>
                <p class="card-text mt-auto">
                  截止日期: <span class="text-danger">${a.due}</span>
                </p>
                <div class="progress" style="height: 20px;">
                  <div class="progress-bar bg-success" role="progressbar" style="width: ${a.submitted/a.total*100}%">${a.submitted}/${a.total}</div>
                </div>
              </div>
            </div>
          </div>`).join('');
      },
  
      exams() {
        const container = document.getElementById('exams-table-container');
        if(!container) return;
        container.innerHTML = `
          <table class="table table-hover">
            <thead><tr><th>课程</th><th>考试名称</th><th>日期</th><th>状态</th><th>操作</th></tr></thead>
            <tbody>
            ${mockData.exams.map(e => `
              <tr>
                <td>${e.course}</td><td>${e.title}</td><td>${e.date}</td>
                <td><span class="badge text-bg-${e.status === '已结束' ? 'secondary' : 'success'}">${e.status}</span></td>
                <td><button class="btn btn-sm btn-outline-info">考情分析</button></td>
              </tr>`).join('')}
            </tbody>
          </table>`;
      },
  
      messages() {
        const messageList = document.getElementById('message-list');
        if (messageList) {
          messageList.innerHTML = mockData.messages.map(msg => `
            <a href="#" class="list-group-item list-group-item-action ${msg.unread ? 'fw-bold' : ''}" data-message-id="${msg.id}">
              <div class="d-flex w-100 justify-content-between">
                <h6 class="mb-1">${msg.sender}</h6>
                <small class="text-secondary">${msg.time}</small>
              </div>
              <p class="mb-1 text-truncate">${msg.subject}</p>
            </a>
          `).join('');
        }
      },
  
      renderAll() {
        Object.values(this).forEach(renderer => {
          if (typeof renderer === 'function' && renderer !== this.renderAll) {
            renderer();
          }
        });
      }
    };
  
  
    /**
     * =================================================================================
     * EVENT LISTENERS & MANAGERS
     * =================================================================================
     */
    const themeManager = {
      init() {
        const savedTheme = localStorage.getItem('theme') || 'dark';
        DOM.themeSwitch.checked = savedTheme === 'dark';
        this.applyTheme();
        DOM.themeSwitch.addEventListener('change', () => this.applyTheme());
      },
      applyTheme() {
        const isDarkMode = DOM.themeSwitch.checked;
        DOM.body.className = isDarkMode ? 'dark-mode' : 'light-mode';
        localStorage.setItem('theme', isDarkMode ? 'dark' : 'light');
        renderers.renderAll(); // Redraw charts with new theme colors
      }
    };
  
    const navigationManager = {
      init() {
        DOM.navLinks.forEach(link => link.addEventListener('click', e => this.handleNavClick(e)));
        DOM.mobileNavToggle.addEventListener('click', () => DOM.sidebar.classList.toggle('is-open'));
      },
      handleNavClick(e) {
        e.preventDefault();
        const link = e.currentTarget;
        const targetId = link.getAttribute('data-target');
        DOM.navLinks.forEach(navLink => navLink.classList.remove('active'));
        link.classList.add('active');
        DOM.contentSections.forEach(section => section.style.display = 'none');
        document.getElementById(targetId).style.display = 'block';
        if (DOM.sidebar.classList.contains('is-open')) {
          DOM.sidebar.classList.remove('is-open');
        }
      }
    };
  
    const actionManager = {
      init() {
        DOM.addDocBtn.addEventListener('click', () => DOM.addDocModal.show());
        DOM.addDocForm.addEventListener('submit', e => this.handleDocSubmit(e));
        DOM.exportDataBtn.addEventListener('click', () => this.exportData());
        DOM.clearCacheBtn.addEventListener('click', () => this.clearCache());
        document.body.addEventListener('click', e => {
          if (e.target.closest('.edit-btn')) showToast('编辑功能待后端接入', 'info');
          if (e.target.closest('.delete-btn')) showToast('删除功能待后端接入', 'warning');
        });
      },
      handleDocSubmit(e) {
        e.preventDefault();
        const newDoc = {
          id: `KB${mockData.knowledgeBase.length + 1}`,
          title: DOM.addDocForm.querySelector('#doc-title').value,
          author: DOM.addDocForm.querySelector('#doc-author').value,
          created_at: new Date().toISOString().split('T')[0],
          views: 0,
          tags: DOM.addDocForm.querySelector('#doc-tags').value.split(',').map(t => t.trim())
        };
        mockData.knowledgeBase.unshift(newDoc); // Add to top
        renderers.knowledgeBase();
        DOM.addDocModal.hide();
        showToast('新文档添加成功!', 'success');
        DOM.addDocForm.reset();
      },
      exportData() {
        const dataStr = JSON.stringify(mockData, null, 2);
        const blob = new Blob([dataStr], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'teacher-agent-data.json';
        a.click();
        URL.revokeObjectURL(url);
        showToast('数据已开始导出', 'success');
      },
      clearCache() {
        localStorage.removeItem('theme');
        localStorage.removeItem('form_draft_teacherName'); // Example
        showToast('本地缓存已清空', 'warning');
        // Optionally reset form fields here
      }
    };
  
    const messageManager = {
      init() {
          document.getElementById('message-list').addEventListener('click', e => {
              const link = e.target.closest('a');
              if(link) this.showMessageDetail(link.dataset.messageId);
          });
          DOM.backToListBtn.addEventListener('click', () => this.showList());
      },
      showMessageDetail(id) {
          const msg = mockData.messages.find(m => m.id === id);
          if (!msg) return;
          document.getElementById('message-content').innerHTML = msg.content;
          DOM.messageListContainer.style.display = 'none';
          DOM.messageDetailContainer.style.display = 'block';
      },
      showList() {
          DOM.messageListContainer.style.display = 'block';
          DOM.messageDetailContainer.style.display = 'none';
      }
    };
  
  
    /**
     * =================================================================================
     * APP INITIALIZATION
     * =================================================================================
     */
    themeManager.init();
    navigationManager.init();
    actionManager.init();
    messageManager.init();
    renderers.renderAll();
  });