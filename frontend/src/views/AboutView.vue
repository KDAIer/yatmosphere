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
            <h3>开发团队：1111</h3>
            <h3>项目开始时间: {{ teamIntro.established }}</h3>
            <h3>团队规模: 10人</h3>
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
        <h3 class="category-title">其他职能</h3>
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
          <p class="copyright">© 2024 AtmoTech. 保留所有权利。</p>
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
            <p>支持操作系统: iOS 12+, Android 8.0+, Windows 10+, macOS 10.15+</p>
            <p>推荐浏览器: Chrome 最新版, Safari 最新版</p>
          </div>
          <div class="update-info">
            <h3>更新日志</h3>
            <p>v1.2.3 (2024-03-15)</p>
            <ul>
              <li>新增设备离线通知功能</li>
              <li>优化了仪表盘加载速度</li>
              <li>修复了场景模式保存问题</li>
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
        description: "AtmoTech智能家居研发团队",
        established: "2023年1月",
        memberCount: 8,
        details: "我们是一支专注于智能家居解决方案的跨职能团队，由资深工程师和设计师组成，致力于打造安全、易用、高效的智能家居控制系统。团队成员来自国内外知名高校，拥有丰富的物联网和软件开发经验。"
      },
      teamMembers: {
        management: [
          {
            name: "马岱",
            role: "项目总经理",
            email: "madai@atmotch.com",
            description: "负责项目整体规划、团队管理以及参与模块协作设计"
          }
        ],
        frontend: [
          {
            name: "张管文",
            role: "前端开发工程师",
            email: "zhangguanwen@atmotch.com",
            description: "疯狂星期四vme50"
          },
          {
            name: "谢泽中",
            role: "前端开发工程师",
            email: "xiezezhong@atmotch.com",
            description: "负责阿巴阿巴"
          },
          {
            name: "汪丁宇洋",
            role: "前端开发工程师",
            email: "wangdingyuyang@atmotch.com",
            description: "负责阿巴阿巴"
          }
        ],
        backend: [
          {
            name: "庄云皓",
            role: "后端开发工程师",
            email: "zhuangyunhao@atmotch.com",
            description: "负责阿巴阿巴"
          },
          {
            name: "金鹏飞",
            role: "后端开发工程师",
            email: "jinpengfei@atmotch.com",
            description: "负责阿巴阿巴"
          }
        ],
        device: [
          {
            name: "潘文磊",
            role: "设备开发工程师",
            email: "panwenlei@atmotch.com",
            description: "负责阿巴阿巴"
          },
          {
            name: "张贞蔚",
            role: "设备开发工程师",
            email: "zhangzhenwei@atmotch.com",
            description: "负责阿巴阿巴"
          }
        ],
        design: [
          {
            name: "潘致远",
            role: "UI/UX设计师",
            email: "panzhiyuan@atmotch.com",
            description: "负责阿巴阿巴"
          }
        ],
        others: [
          {
            name: "黄集瑞",
            role: "安全工程师",
            email: "huangjirui@atmotch.com",
            description: "负责阿巴阿巴"
          }
        ]
      },
      activeFAQ: null,
      faqs: [
        {
          question: "如何添加新设备到系统?",
          answer: "进入设备管理页面，点击右上角的'+'按钮，选择设备类型并按照向导完成添加流程。"
        },
        {
          question: "忘记登录密码怎么办?",
          answer: "在登录页面点击'忘记密码'链接，输入注册邮箱接收重置链接，按照邮件指引重置密码。"
        },
        {
          question: "如何与家人共享设备控制权?",
          answer: "在'家庭管理'页面添加家庭成员，为每个成员分配相应的设备权限。"
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
