<!-- src/views/PolicyView.vue -->
<template>
  <div class="policy-container">
    <button @click="goBack" class="back-btn">← 返回</button>
    <h1 class="policy-title">{{ title }}</h1>
    <div class="policy-meta">
      <span>生效日期：{{ effectiveDate }}</span>
      <span v-if="lastUpdated"> | 最近更新：{{ lastUpdated }}</span>
    </div>
    <div class="policy-content" v-html="contentHtml"></div>
  </div>
</template>

<script>
export default {
  name: 'PolicyView',
  props: {
    type: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      // 可以根据需要调整生效日期和最近更新日期
      effectiveDateMap: {
        privacy: '2025年6月12日',
        terms: '2025年6月12日',
        license: '2025年6月12日'
      },
      lastUpdatedMap: {
        privacy: '2025年6月12日',
        terms: '2025年6月12日',
        license: '2025年6月12日'
      }
    }
  },
  computed: {
    title() {
      switch (this.type) {
        case 'privacy': return '隐私政策'
        case 'terms': return '服务条款'
        case 'license': return '开源许可证'
        default: return '政策'
      }
    },
    effectiveDate() {
      return this.effectiveDateMap[this.type] || ''
    },
    lastUpdated() {
      return this.lastUpdatedMap[this.type] || ''
    },
    contentHtml() {
      // 直接返回 HTML 字符串，可改为从单独 Markdown/JSON 文件或 CMS 加载并转换
      if (this.type === 'privacy') {
        return `
          <h2>1. 前言</h2>
          <p>欢迎使用 Yatmosphere 智能家居控制系统（以下简称“本产品”或“本平台”）。我们非常重视用户隐私，请在使用前仔细阅读本隐私政策。使用本产品即表示您同意本政策。</p>

          <h2>2. 我们如何收集信息</h2>
          <ol>
            <li><strong>用户提供：</strong>如注册账户时填写的姓名、邮箱、手机号等；设备绑定/共享时需要的家庭成员信息；反馈与支持时提交的内容。</li>
            <li><strong>设备与使用信息：</strong>为实现智能控制功能，平台可能收集设备型号、固件版本、设备状态数据、操作日志（如场景启用时间、操作时间戳等）、网络状态信息等。</li>
            <li><strong>统计与日志：</strong>收集用户行为统计、访问日志、错误日志，用于分析优化体验、故障诊断与性能提升。</li>
            <li><strong>第三方授权：</strong>若您通过第三方服务（如社交登录、第三方云服务）接入，本平台仅在您授权范围内获取必要信息，并受相关协议和隐私约束。</li>
          </ol>

          <h2>3. 信息使用目的</h2>
          <ul>
            <li>提供并改进基础功能：如设备管理、远程控制、智能场景执行等。</li>
            <li>优化用户体验：通过分析使用习惯、推荐常用场景、界面改进等。</li>
            <li>故障诊断与技术支持：收集日志与状态信息，帮助定位问题并提供支持。</li>
            <li>安全保障：检测异常行为、预防安全风险、保障账户和设备安全。</li>
            <li>通知与消息：向您发送重要通知、版本更新、活动信息等。</li>
          </ul>

          <h2>4. 信息共享与披露</h2>
          <p>未经用户明确同意，本平台不会将个人身份信息分享给无关第三方，但以下情况除外：</p>
          <ul>
            <li>法律法规或司法机关要求时；</li>
            <li>与我们签署保密协议的合作伙伴，仅为提供本服务所必需，并采取严格安全措施；</li>
            <li>如发生重大安全事件，需要向安全机构或用户通报时。</li>
          </ul>
          <p>对于需要共享给第三方的数据，我们将进行必要的脱敏或匿名化处理。</p>

          <h2>5. 数据存储与安全</h2>
          <ol>
            <li><strong>存储：</strong>用户数据会存储在我们或可信云服务商的服务器上，地理位置及存储期限遵循相关法律法规及公司内部安全策略。</li>
            <li><strong>加密与访问控制：</strong>传输过程中使用 HTTPS/TLS 加密；存储敏感数据时使用加密存储；只有授权人员和系统模块可访问。</li>
            <li><strong>安全审计：</strong>定期或不定期进行安全测试与审计，以发现并修复潜在漏洞。</li>
            <li><strong>备份与恢复：</strong>建立定期备份机制，确保在故障或意外情况下能进行数据恢复。</li>
          </ol>

          <h2>6. Cookie 和类似技术</h2>
          <p>如平台 Web 端或管理后台使用 Cookie、LocalStorage 等技术收集和存储少量信息，以提升功能和体验（如保持登录状态、分析访问等）。您可在浏览器设置中管理或删除 Cookie，但这可能影响某些功能。</p>

          <h2>7. 未成年人保护</h2>
          <p>本平台不针对未成年人设计。如您为未成年人，请在监护人指导和同意下使用。如发现未成年人未经监护人允许提供个人信息，请及时联系我们删除相关信息。</p>

          <h2>8. 用户权利</h2>
          <ol>
            <li>访问与更正：您可查看、更新或更正您的个人信息；</li>
            <li>删除：在法律允许范围内，您可申请删除账户及其关联数据；</li>
            <li>撤回同意：对某些非必要数据处理，您可以撤回同意；</li>
            <li>导出：根据法律规定，您有权请求导出个人数据；</li>
            <li>投诉与查询：若对隐私政策或数据处理有疑问，可联系 madai@mail2.sysu.edu.cn 查询或投诉。</li>
          </ol>

          <h2>9. 第三方链接与服务</h2>
          <p>平台可能集成第三方功能或链接（如地图服务、语音助理、云存储等）。这些第三方具备独立隐私政策，请您知悉并遵守相应条款。</p>

          <h2>10. 政策更新与通知</h2>
          <p>如本隐私政策发生重大变更，我们将在应用内公告或通过邮箱通知，并在生效前给予合理提示。继续使用即视为接受更新。</p>

          <h2>11. 联系我们</h2>
          <p>如有问题，请发送邮件至：<a href="mailto:madai@mail2.sysu.edu.cn">madai@mail2.sysu.edu.cn</a></p>
          <p>联系地址：中山大学广州校区东校园计算机学院</p>
        `
      } else if (this.type === 'terms') {
        return `
          <h2>1. 接受条款</h2>
          <p>本服务条款（以下简称“本条款”）适用于您使用 Yatmosphere 智能家居控制系统及相关服务。请在使用前仔细阅读，使用即表示您已同意本条款。</p>

          <h2>2. 服务内容</h2>
          <p>Yatmosphere 提供智能设备接入、集中管理、远程控制、智能场景、数据统计与分析等功能。平台有权根据业务需要调整、更新或终止部分功能，且无需另行通知，但会尽量提前公告并做好兼容或替代方案。</p>

          <h2>3. 账户注册与管理</h2>
          <ol>
            <li>您需提供真实、准确、完整的信息进行注册，并妥善保管账户凭证；</li>
            <li>账户仅限本人使用，若发生非本人使用导致的损失，平台不承担责任；</li>
            <li>如发现账户被未经授权使用或存在安全风险，请立即通知平台；</li>
            <li>平台有权在发现违规或安全风险时，对账户采取限制、暂停或终止服务等措施。</li>
          </ol>

          <h2>4. 用户义务</h2>
          <ul>
            <li>遵守国家法律法规及公共道德，不得利用本平台进行违法违规活动；</li>
            <li>不得干扰、破坏平台系统、服务器或其他用户的正常使用，不得传播恶意代码；</li>
            <li>对于接入的第三方设备或服务，您需确保其合法合规，并自行承担相关风险；</li>
            <li>及时更新并妥善保存设备固件和软件版本，以免因设备问题引发安全风险；</li>
            <li>不得通过非官方渠道对平台进行逆向、解密、篡改或衍生二次产品。</li>
          </ul>

          <h2>5. 知识产权</h2>
          <p>平台及其相关文档、界面、图标、商标、源代码等权利归 Yatmosphere 团队或相关权利人所有。未经授权，不得复制、修改、发布、传播、销售或以其他方式利用。</p>

          <h2>6. 隐私与数据安全</h2>
          <p>平台会按照隐私政策收集、使用、存储和保护您的个人信息。您同意平台根据隐私政策处理相关信息。</p>

          <h2>7. 免责声明</h2>
          <p>平台在合理商业和技术可行范围内提供服务，但不保证服务在任何时间段均无中断、无故障或满足所有特定需求；对因不可抗力、网络故障或第三方服务问题导致的损失，平台不承担责任，但会尽力协助恢复。</p>

          <h2>8. 责任限制</h2>
          <p>在适用法律允许范围内，平台对因使用或无法使用服务而产生的任何间接、附带、特殊或后续性损失不承担责任。若因平台故意或重大过失导致损失，平台责任以实际直接损失为限。</p>

          <h2>9. 服务更新与终止</h2>
          <ol>
            <li>平台可定期更新或升级系统功能，并可能需要您进行客户端或设备端更新；</li>
            <li>若您违反本条款，平台有权限制或终止您的部分或全部服务；</li>
            <li>您可随时申请注销账户，但注销前需结清相关费用或完成必要流程；注销后，相关数据可在法律规定或平台政策范围内保留或删除。</li>
          </ol>

          <h2>10. 付费服务</h2>
          <p>若平台提供增值或付费服务，需另行签订协议或在平台内明确显示价格与服务内容。您支付的费用一旦确认，若无法律或平台规定的退费情形，一般不予退还。</p>

          <h2>11. 法律适用与争议解决</h2>
          <p>本条款适用中华人民共和国法律。如发生争议，双方应友好协商；协商不成时，可向平台所在地有管辖权的法院提起诉讼。</p>

          <h2>12. 其他</h2>
          <p>若本条款某一条款被视为无效或不可执行，不影响其他条款的效力。本条款未尽事宜，平台有权随时更新并在生效前公告。</p>
        `
      } else if (this.type === 'license') {
        return `
          <pre><code>
Copyright (c) 2025 Yatmosphere 团队

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
          </code></pre>
        `
      } else {
        return `<p>未找到对应页面。</p>`
      }
    }
  },
  methods: {
    goBack() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
.policy-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 16px;
}
.back-btn {
  margin-bottom: 12px;
  background: none;
  border: none;
  color: #007AFF;
  font-size: 14px;
  cursor: pointer;
}
.policy-title {
  margin-bottom: 8px;
}
.policy-meta {
  margin-bottom: 16px;
  font-size: 14px;
  color: #666;
}
.policy-meta span {
  margin-right: 8px;
}
.policy-content h2 {
  margin-top: 20px;
}
.policy-content p, .policy-content li, .policy-content ol, .policy-content ul, .policy-content pre {
  line-height: 1.6;
}
.policy-content pre {
  background: #f5f5f5;
  padding: 12px;
  overflow-x: auto;
}
.policy-content code {
  font-family: Consolas, "Courier New", monospace;
}
</style>
