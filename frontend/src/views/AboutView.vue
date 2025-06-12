<template>
  <div class="about-container">
    <header class="about-header">
      <h1>关于Yatmosphere</h1>
      <p class="version">当前版本: v1.2.3</p>
    </header>

    <main class="about-content">
      <!-- 应用简介 -->
      <section class="about-section">
        <h2 @click="toggleSection('about')">
          <span class="toggle-icon">{{ expandedSections.about ? '▼' : '▶' }}</span>
          应用简介
        </h2>
        <div v-show="expandedSections.about" class="section-content">
          <p><strong>Yatmosphere</strong>智能家居控制系统，是一款集中管理智能设备的平台。</p>
          <br>
          <div class="developer-info">
            <h3>开发团队：Yatmosphere软工小组</h3>
            <h3>启动时间：2025年5月</h3>
            <h3>团队规模：10人</h3>
            <!-- <p>{{ teamIntro.description }}</p> -->
            <!-- <p>成立时间: {{ teamIntro.established }}</p>
            <p>团队规模: {{ teamIntro.memberCount }}人</p> -->
            <!-- <p>项目开始时间: {{ teamIntro.established }}</p> -->

          </div>
        </div>
      </section>

<!-- 开发团队 -->
<section class="about-section">
  <h2 @click="toggleSection('team')">
    <span class="toggle-icon">{{ expandedSections.team ? '▼' : '▶' }}</span>
    开发团队
  </h2>

  <div v-show="expandedSections.team" class="section-content">
    <div class="team-intro" v-if="teamIntro.details">
      <h3>团队简介</h3>
      <p>{{ teamIntro.details }}</p>
    </div>

    <div class="team-container">
      <!-- 项目管理 -->
      <div class="team-category">
        <h3 class="category-title">项目管理</h3>
        <div class="member-cards">
          <div class="member-card" v-for="member in teamMembers.management" :key="member.name">
            <div class="card-header">
              <h4 class="member-name">{{ member.name }}</h4>
              <span class="member-role">{{ member.role }}</span>
            </div>
            <div class="card-body">
              <p class="member-desc" v-if="member.description">{{ member.description }}</p>
            </div>
            <div class="card-footer">
              <a :href="`mailto:${member.email}`" class="contact-email">
                <span class="contact-icon">✉️</span> {{ member.email }}
              </a>
            </div>
          </div>
        </div>
      </div>

      <!-- 前端开发 -->
      <div class="team-category">
        <h3 class="category-title">前端开发</h3>
        <div class="member-cards">
          <div class="member-card" v-for="member in teamMembers.frontend" :key="member.name">
            <div class="card-header">
              <h4 class="member-name">{{ member.name }}</h4>
              <span class="member-role">{{ member.role }}</span>
            </div>
            <div class="card-body">
              <p class="member-desc" v-if="member.description">{{ member.description }}</p>
            </div>
            <div class="card-footer">
              <a :href="`mailto:${member.email}`" class="contact-email">
                <span class="contact-icon">✉️</span> {{ member.email }}
              </a>
            </div>
          </div>
        </div>
      </div>

      <!-- 后端开发 -->
      <div class="team-category">
        <h3 class="category-title">后端开发</h3>
        <div class="member-cards">
          <div class="member-card" v-for="member in teamMembers.backend" :key="member.name">
            <div class="card-header">
              <h4 class="member-name">{{ member.name }}</h4>
              <span class="member-role">{{ member.role }}</span>
            </div>
            <div class="card-body">
              <p class="member-desc" v-if="member.description">{{ member.description }}</p>
            </div>
            <div class="card-footer">
              <a :href="`mailto:${member.email}`" class="contact-email">
                <span class="contact-icon">✉️</span> {{ member.email }}
              </a>
            </div>
          </div>
        </div>
      </div>
      <!-- 设备开发 -->
      <div class="team-category">
        <h3 class="category-title">设备开发</h3>
        <div class="member-cards">
          <div class="member-card" v-for="member in teamMembers.device" :key="member.name">
            <div class="card-header">
              <h4 class="member-name">{{ member.name }}</h4>
              <span class="member-role">{{ member.role }}</span>
            </div>
            <div class="card-body">
              <p class="member-desc" v-if="member.description">{{ member.description }}</p>
            </div>
            <div class="card-footer">
              <a :href="`mailto:${member.email}`" class="contact-email">
                <span class="contact-icon">✉️</span> {{ member.email }}
              </a>
            </div>
          </div>
        </div>
      </div>
      <!-- 设计团队 -->
      <div class="team-category">
        <h3 class="category-title">UI/UX设计</h3>
        <div class="member-cards">
          <div class="member-card" v-for="member in teamMembers.design" :key="member.name">
            <div class="card-header">
              <h4 class="member-name">{{ member.name }}</h4>
              <span class="member-role">{{ member.role }}</span>
            </div>
            <div class="card-body">
              <p class="member-desc" v-if="member.description">{{ member.description }}</p>
            </div>
            <div class="card-footer">
              <a :href="`mailto:${member.email}`" class="contact-email">
                <span class="contact-icon">✉️</span> {{ member.email }}
              </a>
            </div>
          </div>
        </div>
      </div>

      <!-- 其他职能 -->
      <div class="team-category">
        <h3 class="category-title">系统安全</h3>
        <div class="member-cards">
          <div class="member-card" v-for="member in teamMembers.others" :key="member.name">
            <div class="card-header">
              <h4 class="member-name">{{ member.name }}</h4>
              <span class="member-role">{{ member.role }}</span>
            </div>
            <div class="card-body">
              <p class="member-desc" v-if="member.description">{{ member.description }}</p>
            </div>
            <div class="card-footer">
              <a :href="`mailto:${member.email}`" class="contact-email">
                <span class="contact-icon">✉️</span> {{ member.email }}
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>



      <!-- 帮助中心 -->
      <section class="about-section">
        <h2 @click="toggleSection('help')">
          <span class="toggle-icon">{{ expandedSections.help ? '▼' : '▶' }}</span>
          帮助中心
        </h2>
        <div v-show="expandedSections.help" class="section-content">
          <div class="faq-list">
            <div v-for="(item, index) in faqs" :key="index" class="faq-item">
              <div class="faq-question" @click="toggleFAQ(index)">
                {{ item.question }}
                <span class="faq-toggle">{{ activeFAQ === index ? '▲' : '▼' }}</span>
              </div>
              <div v-if="activeFAQ === index" class="faq-answer">
                {{ item.answer }}
              </div>
            </div>
          </div>
          <button class="feedback-btn" @click="openFeedback">提交使用反馈</button>
        </div>
      </section>

      <!-- 法律条款 -->
      <section class="about-section">
        <h2 @click="toggleSection('legal')">
          <span class="toggle-icon">{{ expandedSections.legal ? '▼' : '▶' }}</span>
          法律条款
        </h2>
        <div v-show="expandedSections.legal" class="section-content">
          <ul class="legal-links">
            <li><a @click="openPolicy('privacy')">隐私政策</a></li>
            <li><a @click="openPolicy('terms')">服务条款</a></li>
            <li><a @click="openPolicy('license')">开源许可证</a></li>
          </ul>
          <p class="copyright">© 2025 Yatmosphere软工小组. 保留所有权利。</p>
        </div>
      </section>

      <!-- 技术支持 -->
      <section class="about-section">
        <h2 @click="toggleSection('support')">
          <span class="toggle-icon">{{ expandedSections.support ? '▼' : '▶' }}</span>
          技术支持
        </h2>
        <div v-show="expandedSections.support" class="section-content">
          <div class="compatibility">
            <h3>兼容性信息</h3>
            <p>支持操作系统: iOS 12+, Android 8.0+, Windows 7+, macOS 10.15+</p>
            <p>推荐浏览器: Google浏览器 最新版, edge 最新版</p>
          </div>
          <div class="update-info">
            <h3>更新日志</h3>
            <p>v1.2.3 (2025-06-11)</p>
            <ul>
              <li>新增智能机器人模块</li>
              <li>优化了页面适配</li>
              <li>修复了设备相应问题</li>
            </ul>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script>
export default {
  data() {
    return {


      expandedSections: {
        about: true,
        team: false,
        help: false,
        legal: false,
        support: false
      },
      teamIntro: {
        description: "Yatmosphere软工小组",
        established: "2025年5月",
        memberCount: 10,
        details: "我们是一支专注于智能家居解决方案的跨职能团队，由中山大学计算机学院学生组成，致力于打造安全、易用、高效、先进的智能家居控制系统。"
      },
      teamMembers: {
        management: [
          {
            name: "马岱",
            role: "项目总经理",
            email: "madai@mail2.sysu.edu.cn",
            description: "Keep your head up in failure, and your head down in success."
          }
        ],
        frontend: [
          {
            name: "张管文Ambition",
            role: "前端开发工程师",
            email: "zhanggw26@mail2.sysu.edu.cn",
            description: "疯狂星期四vme50"
          },
          {
            name: "谢泽中",
            role: "前端开发工程师",
            email: "xiezzh5@mail2.sysu.edu.cn",
            description: "敬请期待"
          },
          {
            name: "汪丁宇洋",
            role: "前端开发工程师",
            email: "1173573823@qq.com",
            description: "XX，启动！"
          }
        ],
        backend: [
          {
            name: "庄云皓",
            role: "后端开发工程师",
            email: "zhuangyh9@mail2.sysu.edu.cn",
            description: "这片星空，只有流星划过"
          },
          {
            name: "金鹏飞",
            role: "后端开发工程师",
            email: "jinpf5@mail2.sysu.edu.cn",
            description: "我有一个小可爱"
          }
        ],
        device: [
          {
            name: "潘文磊",
            role: "设备开发工程师",
            email: "panwlei@mail2.sysu.edu.cn",
            description: "请输入文本"
          },
          {
            name: "张贞蔚",
            role: "设备开发工程师",
            email: "zhangzhw58@mail2.sysu.edu.cn",
            description: "对的"
          }
        ],
        design: [
          {
            name: "潘致远",
            role: "UI/UX设计师",
            email: "1525522950@qq.com",
            description: "哈基咪哈基，哈基咪摸南北绿多，阿西噶哈雅酷奶农，哈基咪曼波亚不亚不有待有待哦吗吉利曼波"
          }
        ],
        others: [
          {
            name: "黄集瑞（hjr）",
            role: "全能开发工程师",
            email: "huang17359686189@163.com",
            description: "王不见王"
           },
        ]
      },
      activeFAQ: null,
      faqs: [
        {
          question: "如何添加新设备到系统?",
          answer: "进入设备管理页面，点击的\"添加设备\"按钮，选择设备类型并按照向导完成添加流程。"
        },
        {
          question: "如何创建智能场景?",
          answer: "进入主界面智能场景，点击的\"自定义场景\"按钮，选择设备类型并按照向导完成添加流程。"
        },
        {
          question: "如何与家人共享设备控制权?",
          answer: "在家庭管理页面添加家庭成员，为每个成员分配相应的设备权限。"
        },
        {
          question: "我还有其他问题",
          answer: "可以进入智能助手页面，询问智能助手相关问题。"
        }
      ]
    }
  },
  methods: {

    toggleSection(section) {
      this.expandedSections[section] = !this.expandedSections[section]
    },
    toggleFAQ(index) {
      this.activeFAQ = this.activeFAQ === index ? null : index
    },
    openFeedback() {
      // 打开反馈对话框
      this.$router.push('/feedback')
    },
    openPolicy(type) {
      // 打开对应政策页面
      window.open(`/policies/${type}.html`, '_blank')
    }
  }
}
</script>

<style scoped src="./AboutView.css"></style>
