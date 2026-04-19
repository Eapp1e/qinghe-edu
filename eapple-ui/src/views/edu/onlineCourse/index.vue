<template>
  <div class="app-container online-course-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">Online Learning</span>
        <h1>网课中心</h1>
        <p>整合适合中小学阶段的免费学习平台、科普网站与优质视频入口，便于学生课后延伸学习与兴趣拓展。</p>
      </div>
      <div class="hero-stats hero-stats--compact">
        <div class="stat-card">
          <span>官方平台</span>
          <strong>{{ officialCount }}</strong>
        </div>
        <div class="stat-card">
          <span>视频资源</span>
          <strong>{{ videoCount }}</strong>
        </div>
      </div>
    </section>

    <section class="ai-recommend-panel">
      <div class="ai-recommend-head">
        <div>
          <h3>网课推荐</h3>
          <p>输入想学的方向，系统会结合当前网课资源给出更匹配的学习入口。</p>
        </div>
        <el-button
          class="recommend-trigger"
          type="primary"
          icon="el-icon-magic-stick"
          :loading="aiLoading"
          @click="handleAiRecommend"
        >
          {{ aiLoading ? 'AI 推荐中...' : '生成推荐' }}
        </el-button>
      </div>
      <el-input
        class="recommend-input"
        v-model="interestInput"
        type="textarea"
        :rows="3"
        placeholder="例如：我想学 Scratch 编程、数学思维、英语绘本阅读、科学实验"
      />
      <div v-if="aiRecommendations.length" class="ai-result-grid">
        <article
          v-for="item in aiRecommendations"
          :key="item.title + item.link"
          class="ai-result-card"
        >
          <div class="ai-result-head">
            <div>
              <strong>{{ item.title }}</strong>
              <span class="ai-result-source">{{ item.source || '智能筛选' }}</span>
            </div>
            <span>AI 推荐</span>
          </div>
          <p>{{ item.reason }}</p>
          <a
            class="resource-link resource-link--inline"
            :href="item.link"
            target="_blank"
            rel="noopener noreferrer"
          >
            跳转学习
          </a>
        </article>
      </div>
    </section>

    <section class="toolbar-panel">
      <div class="toolbar-filters">
        <button
          v-for="item in stageOptions"
          :key="item.value"
          type="button"
          :class="['filter-chip', { active: activeStage === item.value }]"
          @click="activeStage = item.value"
        >
          {{ item.label }}
        </button>
      </div>
      <div class="toolbar-filters secondary">
        <button
          v-for="item in categoryOptions"
          :key="item.value"
          type="button"
          :class="['filter-chip', { active: activeCategory === item.value }]"
          @click="activeCategory = item.value"
        >
          {{ item.label }}
        </button>
      </div>
    </section>

    <section class="resource-grid resource-grid--top">
      <article
        v-for="item in filteredResources"
        :key="item.title"
        class="resource-card"
      >
        <div class="resource-head">
          <div>
            <span class="resource-source">{{ item.source }}</span>
            <h3>{{ item.title }}</h3>
          </div>
          <span :class="['resource-type', item.typeClass]">{{ item.typeLabel }}</span>
        </div>
        <p class="resource-desc">{{ item.description }}</p>
        <div class="resource-meta">
          <span>{{ item.stageLabel }}</span>
          <span>{{ item.category }}</span>
          <span>{{ item.freeText }}</span>
        </div>
        <div class="resource-tags">
          <span v-for="tag in item.tags" :key="tag">{{ tag }}</span>
        </div>
        <button type="button" class="resource-link" @click="openResource(item)">
          立即进入
        </button>
      </article>
    </section>
  </div>
</template>

<script>
import { recommendOnlineResources } from '@/api/edu/ai'

export default {
  name: 'EduOnlineCourse',
  data() {
    return {
      activeStage: 'all',
      activeCategory: 'all',
      interestInput: '',
      aiLoading: false,
      aiRecommendations: [],
      stageOptions: [
        { label: '全部阶段', value: 'all' },
        { label: '小学', value: 'primary' },
        { label: '初中', value: 'middle' },
        { label: '通用拓展', value: 'common' }
      ],
      categoryOptions: [
        { label: '全部类型', value: 'all' },
        { label: '综合学习', value: '综合课程' },
        { label: '阅读积累', value: '阅读资源' },
        { label: '数学提升', value: '数学提升' },
        { label: '英语拓展', value: '英语拓展' },
        { label: '编程启蒙', value: '编程启蒙' },
        { label: '科学探究', value: '科学探究' },
        { label: '美育创作', value: '美育素养' }
      ],
      resources: [
        {
          title: '国家中小学智慧教育平台',
          source: '教育部平台',
          description: '覆盖小学、初中到高中多学段资源，适合同步复习、专题拓展和自主学习。',
          category: '综合课程',
          stage: 'common',
          stageLabel: '小学 / 初中 / 高中',
          sourceType: 'official',
          typeLabel: '官方平台',
          typeClass: 'official',
          freeText: '免费使用',
          tags: ['同步学习', '专题资源', '官方权威'],
          link: 'https://basic.smartedu.cn/'
        },
        {
          title: '国图公开课与少儿资源',
          source: '国家图书馆',
          description: '提供公开课、电子阅读、少儿数字资源和主题阅读入口，适合课外积累与拓展阅读。',
          category: '阅读资源',
          stage: 'common',
          stageLabel: '小学 / 初中',
          sourceType: 'official',
          typeLabel: '官方平台',
          typeClass: 'official',
          freeText: '免费访问',
          tags: ['公开课', '数字阅读', '少儿资源'],
          link: 'http://open.nlc.cn/'
        },
        {
          title: '中国科普博览',
          source: '中科院科普',
          description: '适合拓展科学兴趣，包含公开课、虚拟展馆和面向青少年的丰富科普内容。',
          category: '科学探究',
          stage: 'common',
          stageLabel: '小学高年级 / 初中',
          sourceType: 'official',
          typeLabel: '官方平台',
          typeClass: 'official',
          freeText: '免费使用',
          tags: ['科学公开课', '虚拟博物馆', '中科院资源'],
          link: 'https://kepu.net.cn/'
        },
        {
          title: '中国数字科技馆',
          source: '中国科协平台',
          description: '提供科学课程、实验探究、科普视频与线上展馆资源，适合中小学生做课后科学学习。',
          category: '科学探究',
          stage: 'common',
          stageLabel: '小学 / 初中',
          sourceType: 'official',
          typeLabel: '官方平台',
          typeClass: 'official',
          freeText: '免费使用',
          tags: ['科学课程', '实验探究', '官方科普'],
          link: 'https://www.cdstm.cn/'
        },
        {
          title: '云端国博·国博好课',
          source: '中国国家博物馆',
          description: '整合线上好课、讲堂内容与云展览，适合历史、人文与传统文化主题的延伸学习。',
          category: '综合课程',
          stage: 'common',
          stageLabel: '小学高年级 / 初中',
          sourceType: 'official',
          typeLabel: '官方平台',
          typeClass: 'official',
          freeText: '免费使用',
          tags: ['人文历史', '国博好课', '文化拓展'],
          link: 'https://www.chnmuseum.cn/portals/0/web/zt/ydgb/index.html'
        },
        {
          title: '故宫博物院教育频道',
          source: '故宫博物院',
          description: '提供线上故宫课程、青少年文化活动与传统文化学习内容，适合美育与人文素养拓展。',
          category: '美育素养',
          stage: 'common',
          stageLabel: '小学高年级 / 初中',
          sourceType: 'official',
          typeLabel: '官方平台',
          typeClass: 'official',
          freeText: '免费使用',
          tags: ['传统文化', '线上课程', '美育拓展'],
          link: 'https://www.dpm.org.cn/Events.html'
        },
        {
          title: '国博讲堂',
          source: '中国国家博物馆',
          description: '汇集历史、文物、艺术等主题讲座与课程内容，适合做人文历史方向的专题学习。',
          category: '综合课程',
          stage: 'common',
          stageLabel: '小学高年级 / 初中',
          sourceType: 'official',
          typeLabel: '官方平台',
          typeClass: 'official',
          freeText: '免费使用',
          tags: ['历史讲堂', '文博课程', '人文拓展'],
          link: 'https://m.chnmuseum.cn/sp/gbjt_150/'
        },
        {
          title: '学习强国·教育频道',
          source: '学习强国',
          description: '整合教育资讯、公开课程与综合学习资源，适合作为课后通识学习和拓展入口。',
          category: '综合课程',
          stage: 'common',
          stageLabel: '小学 / 初中',
          sourceType: 'official',
          typeLabel: '官方平台',
          typeClass: 'official',
          freeText: '免费使用',
          tags: ['综合学习', '公开课程', '官方资源'],
          link: 'https://www.xuexi.cn/'
        },
        {
          title: 'Bilibili 数学思维精选',
          source: 'Bilibili',
          description: '聚合数学思维、解题方法和趣味数学视频，适合课后查漏补缺与思维训练。',
          category: '数学提升',
          stage: 'middle',
          stageLabel: '小学高年级 / 初中',
          sourceType: 'video',
          typeLabel: '视频学习',
          typeClass: 'video',
          freeText: '免费检索',
          tags: ['数学思维', '解题方法', '视频课程'],
          link: 'https://search.bilibili.com/all?keyword=%E6%95%B0%E5%AD%A6%E6%80%9D%E7%BB%B4'
        },
        {
          title: 'Bilibili 奥数与逻辑训练',
          source: 'Bilibili',
          description: '适合想进一步提升数学兴趣与逻辑能力的学生，用于课后强化思维训练。',
          category: '数学提升',
          stage: 'middle',
          stageLabel: '小学高年级 / 初中',
          sourceType: 'video',
          typeLabel: '视频学习',
          typeClass: 'video',
          freeText: '免费检索',
          tags: ['奥数启蒙', '逻辑训练', '思维提升'],
          link: 'https://search.bilibili.com/all?keyword=%E5%A5%A5%E6%95%B0%20%E9%80%BB%E8%BE%91%E8%AE%AD%E7%BB%83'
        },
        {
          title: 'Bilibili 少儿编程启蒙',
          source: 'Bilibili',
          description: '适合对编程感兴趣的学生，可从 Scratch、图形化编程到基础逻辑逐步入门。',
          category: '编程启蒙',
          stage: 'primary',
          stageLabel: '小学',
          sourceType: 'video',
          typeLabel: '视频学习',
          typeClass: 'video',
          freeText: '免费检索',
          tags: ['Scratch', '逻辑训练', '兴趣启蒙'],
          link: 'https://search.bilibili.com/all?keyword=Scratch%E5%B0%91%E5%84%BF%E7%BC%96%E7%A8%8B'
        },
        {
          title: 'Scratch 编程案例库',
          source: 'Bilibili',
          description: '覆盖动画、游戏、数学小项目等案例，适合从兴趣尝试逐步过渡到项目创作。',
          category: '编程启蒙',
          stage: 'primary',
          stageLabel: '小学 / 初中起步',
          sourceType: 'video',
          typeLabel: '视频学习',
          typeClass: 'video',
          freeText: '免费检索',
          tags: ['项目案例', '图形化编程', '动手创作'],
          link: 'https://search.bilibili.com/all?keyword=%E5%B0%91%E5%84%BF%E7%BC%96%E7%A8%8Bscratch%20100%E4%B8%AA%E7%BC%96%E7%A8%8B%E6%A1%88%E4%BE%8B'
        },
        {
          title: 'Bilibili 英语绘本与听说',
          source: 'Bilibili',
          description: '适合利用碎片时间做英语启蒙、绘本跟读和口语语感训练。',
          category: '英语拓展',
          stage: 'primary',
          stageLabel: '小学',
          sourceType: 'video',
          typeLabel: '视频学习',
          typeClass: 'video',
          freeText: '免费检索',
          tags: ['英语绘本', '听说训练', '课外拓展'],
          link: 'https://search.bilibili.com/all?keyword=%E8%8B%B1%E8%AF%AD%E7%BB%98%E6%9C%AC'
        },
        {
          title: 'Bilibili 自然拼读与口语启蒙',
          source: 'Bilibili',
          description: '适合小学阶段做自然拼读、跟读和基础表达训练，帮助建立英语语感。',
          category: '英语拓展',
          stage: 'primary',
          stageLabel: '小学',
          sourceType: 'video',
          typeLabel: '视频学习',
          typeClass: 'video',
          freeText: '免费检索',
          tags: ['自然拼读', '跟读训练', '英语语感'],
          link: 'https://search.bilibili.com/all?keyword=%E8%87%AA%E7%84%B6%E6%8B%BC%E8%AF%BB%20%E5%B0%91%E5%84%BF%E8%8B%B1%E8%AF%AD'
        },
        {
          title: 'Bilibili 科学实验与科普',
          source: 'Bilibili',
          description: '通过实验演示和科学解读激发好奇心，适合作为课后探究和主题展示素材。',
          category: '科学探究',
          stage: 'common',
          stageLabel: '小学 / 初中',
          sourceType: 'video',
          typeLabel: '视频学习',
          typeClass: 'video',
          freeText: '免费检索',
          tags: ['实验演示', '科学兴趣', '探究学习'],
          link: 'https://search.bilibili.com/all?keyword=%E7%A7%91%E5%AD%A6%E5%AE%9E%E9%AA%8C%20%E7%A7%91%E6%99%AE'
        },
        {
          title: 'Bilibili 天文与自然科学',
          source: 'Bilibili',
          description: '适合喜欢宇宙、自然和前沿科技的学生做兴趣拓展，培养科学好奇心。',
          category: '科学探究',
          stage: 'common',
          stageLabel: '小学 / 初中',
          sourceType: 'video',
          typeLabel: '视频学习',
          typeClass: 'video',
          freeText: '免费检索',
          tags: ['天文启蒙', '自然科学', '兴趣拓展'],
          link: 'https://search.bilibili.com/all?keyword=%E5%A4%A9%E6%96%87%20%E7%A7%91%E6%99%AE'
        },
        {
          title: 'Bilibili 创意美术与手工',
          source: 'Bilibili',
          description: '适合课后做绘画、手工和创意表达，兼顾审美培养与动手能力训练。',
          category: '美育素养',
          stage: 'primary',
          stageLabel: '小学 / 初中',
          sourceType: 'video',
          typeLabel: '视频学习',
          typeClass: 'video',
          freeText: '免费检索',
          tags: ['创意绘画', '手工制作', '审美表达'],
          link: 'https://search.bilibili.com/all?keyword=%E5%88%9B%E6%84%8F%E7%BE%8E%E6%9C%AF%20%E5%B0%91%E5%84%BF'
        },
        {
          title: 'Bilibili 历史人文拓展',
          source: 'Bilibili',
          description: '适合在课后扩充历史常识与人文视野，帮助学生形成更完整的知识连接。',
          category: '综合课程',
          stage: 'middle',
          stageLabel: '小学高年级 / 初中',
          sourceType: 'video',
          typeLabel: '视频学习',
          typeClass: 'video',
          freeText: '免费检索',
          tags: ['历史启蒙', '人文素养', '拓展阅读'],
          link: 'https://search.bilibili.com/all?keyword=%E5%8E%86%E5%8F%B2%20%E4%BA%BA%E6%96%87%20%E5%B0%91%E5%84%BF'
        }
      ]
    }
  },
  computed: {
    filteredResources() {
      return this.resources.filter(item => {
        const stageMatched = this.activeStage === 'all' || item.stage === this.activeStage || item.stage === 'common'
        const categoryMatched = this.activeCategory === 'all' || item.category === this.activeCategory
        return stageMatched && categoryMatched
      })
    },
    officialCount() {
      return this.resources.filter(item => item.sourceType === 'official').length
    },
    videoCount() {
      return this.resources.filter(item => item.sourceType === 'video').length
    }
  },
  methods: {
    openResource(item) {
      this.openLink(item.link)
    },
    openLink(link) {
      window.open(link, '_blank', 'noopener')
    },
    handleAiRecommend() {
      const interest = (this.interestInput || '').trim()
      if (!interest) {
        this.$modal.msgWarning('请先输入想学习的内容')
        return
      }
      this.aiLoading = true
      this.aiRecommendations = []
      recommendOnlineResources({
        interest,
        resources: this.buildAiPromptResources(interest).map(item => ({
          title: item.title,
          source: item.source,
          category: item.category,
          description: item.description,
          link: item.link
        }))
      }).then(res => {
        const list = Array.isArray(res.data) ? res.data : []
        this.aiRecommendations = list.filter(item => item && item.title && item.link).map(item => ({
          title: item.title,
          source: item.source || '',
          reason: item.reason || '与当前学习兴趣较匹配',
          link: item.link
        }))
        if (!this.aiRecommendations.length) {
          this.aiRecommendations = this.buildLocalRecommendations(interest)
        }
        if (!this.aiRecommendations.length) {
          this.$modal.msgWarning('当前没有匹配到合适资源，可以换个关键词试试')
        }
      }).catch(() => {
        this.aiRecommendations = this.buildLocalRecommendations(interest)
        if (!this.aiRecommendations.length) {
          this.$modal.msgWarning('当前没有匹配到合适资源，可以换个关键词试试')
        }
      }).finally(() => {
        this.aiLoading = false
      })
    },
    buildAiPromptResources(interest) {
      const keywords = this.extractKeywords(interest)
      const scored = this.resources.map(item => {
        const haystack = [
          item.title,
          item.source,
          item.category,
          item.description,
          ...(item.tags || [])
        ].join(' ').toLowerCase()
        let score = 0
        keywords.forEach(word => {
          if (haystack.includes(word)) {
            score += word.length > 2 ? 3 : 1
          }
        })
        if (!score) {
          if (item.category && interest.includes(item.category.replace('提升', '').replace('资源', '').replace('素养', ''))) {
            score += 2
          }
          if (item.title && interest.toLowerCase().includes(item.title.slice(0, 4).toLowerCase())) {
            score += 2
          }
        }
        return { item, score }
      })

      const matched = scored
        .filter(entry => entry.score > 0)
        .sort((a, b) => b.score - a.score)
        .slice(0, 6)
        .map(entry => ({
          ...entry.item,
          description: this.buildPromptSummary(entry.item)
        }))

      if (matched.length) {
        return matched
      }

      return this.resources.slice(0, 4).map(item => ({
        ...item,
        description: this.buildPromptSummary(item)
      }))
    },
    extractKeywords(interest) {
      return interest
        .replace(/[，。；、,.!?！？]/g, ' ')
        .split(/\s+/)
        .map(item => item.trim().toLowerCase())
        .filter(Boolean)
    },
    compressText(text, maxLength) {
      if (!text) {
        return ''
      }
      return text.length > maxLength ? `${text.slice(0, maxLength)}...` : text
    },
    buildPromptSummary(item) {
      const parts = [
        item.category,
        item.stageLabel,
        ...(item.tags || []).slice(0, 3)
      ].filter(Boolean)
      return this.compressText(parts.join(' / '), 40)
    },
    buildLocalRecommendations(interest) {
      const keywords = this.extractKeywords(interest)

      const scored = this.resources.map(item => {
        const haystack = [
          item.title,
          item.source,
          item.category,
          item.description,
          ...(item.tags || [])
        ].join(' ').toLowerCase()
        let score = 0
        keywords.forEach(word => {
          if (haystack.includes(word)) {
            score += word.length > 2 ? 3 : 1
          }
        })
        if (!keywords.length) {
          score = 0
        }
        return { item, score }
      })

      return scored
        .filter(entry => entry.score > 0)
        .sort((a, b) => b.score - a.score)
        .slice(0, 3)
        .map(entry => ({
          title: entry.item.title,
          source: entry.item.source,
          reason: `推荐学习 ${entry.item.category} 方向内容，和“${interest}”较为匹配。`,
          link: entry.item.link
        }))
    }
  }
}
</script>

<style scoped>
.online-course-page {
  position: relative;
  padding-top: 6px;
}

.online-course-page::before,
.online-course-page::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  filter: blur(6px);
  pointer-events: none;
}

.online-course-page::before {
  top: -52px;
  right: 2%;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(38, 232, 183, 0.18), transparent 68%);
}

.online-course-page::after {
  left: -60px;
  bottom: 12%;
  width: 220px;
  height: 220px;
  background: radial-gradient(circle, rgba(91, 187, 255, 0.14), transparent 70%);
}

.hero-panel {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 18px;
  padding: 28px;
  border: 1px solid rgba(103, 224, 214, 0.18);
  border-radius: 26px;
  background:
    radial-gradient(circle at top right, rgba(63, 229, 190, 0.2), transparent 28%),
    radial-gradient(circle at bottom left, rgba(72, 153, 255, 0.16), transparent 24%),
    linear-gradient(135deg, rgba(21, 34, 46, 0.96), rgba(29, 73, 82, 0.92));
  box-shadow:
    0 24px 44px rgba(15, 35, 46, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
}

.hero-copy {
  max-width: 760px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(32, 224, 182, 0.14);
  color: #8ff6dc;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.02em;
  box-shadow: 0 10px 20px rgba(12, 34, 40, 0.2);
}

.hero-copy h1 {
  margin: 16px 0 12px;
  color: #ffffff;
  font-size: 34px;
}

.hero-copy p {
  margin: 0;
  color: rgba(232, 246, 246, 0.78);
  line-height: 1.9;
}

.hero-stats {
  display: grid;
  gap: 12px;
  margin-left: auto;
}

.hero-stats--compact {
  grid-template-columns: repeat(2, 1fr);
  min-width: 250px;
}

.stat-card {
  padding: 18px;
  border: 1px solid rgba(129, 224, 224, 0.14);
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.04));
  box-shadow: 0 16px 28px rgba(11, 32, 40, 0.16), inset 0 1px 0 rgba(255, 255, 255, 0.06);
}

.stat-card span {
  color: rgba(222, 240, 243, 0.72);
  font-size: 13px;
}

.stat-card strong {
  display: block;
  margin-top: 10px;
  color: #ffffff;
  font-size: 28px;
}

.ai-recommend-panel,
.toolbar-panel {
  position: relative;
  z-index: 1;
  margin-bottom: 16px;
  padding: 20px 22px;
  border: 1px solid rgba(157, 232, 233, 0.42);
  border-radius: 22px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.72), rgba(236, 251, 255, 0.52)),
    rgba(255, 255, 255, 0.26);
  box-shadow: 0 22px 40px rgba(41, 130, 141, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(18px) saturate(140%);
}

.ai-recommend-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.ai-recommend-head h3 {
  margin: 14px 0 8px;
  color: #173746;
  font-size: 28px;
  font-family: "STZhongsong", "Noto Serif SC", "Source Han Serif SC", "Songti SC", serif;
  letter-spacing: 0.02em;
}

.ai-recommend-head p {
  margin: 0;
  color: #6f8792;
  line-height: 1.75;
}

.recommend-trigger {
  height: 48px;
  padding: 0 24px;
  border: none;
  border-radius: 18px;
  background: linear-gradient(135deg, #62d4b0 0%, #4aa6f3 100%);
  box-shadow: 0 14px 26px rgba(73, 171, 204, 0.2);
  color: #ffffff;
  font-size: 15px;
  font-weight: 700;
}

.recommend-trigger:hover,
.recommend-trigger:focus {
  background: linear-gradient(135deg, #5bcdaa 0%, #459df0 100%);
  box-shadow: 0 16px 28px rgba(73, 171, 204, 0.24);
  color: #ffffff;
}

.recommend-trigger.is-loading,
.recommend-trigger.is-disabled {
  background: linear-gradient(135deg, #78d8ba 0%, #6eb4f3 100%);
  color: rgba(255, 255, 255, 0.92);
}

.recommend-input /deep/ .el-textarea__inner {
  min-height: 112px;
  padding: 16px 18px;
  border: 1px solid rgba(108, 196, 192, 0.55);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.84);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.72);
  color: #244350;
  font-size: 15px;
  line-height: 1.8;
  transition: border-color 0.22s ease, box-shadow 0.22s ease;
}

.recommend-input /deep/ .el-textarea__inner:focus {
  border-color: rgba(83, 194, 176, 0.9);
  box-shadow: 0 0 0 3px rgba(98, 211, 188, 0.14);
}

.ai-result-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.ai-result-card {
  padding: 16px 18px;
  border: 1px solid rgba(180, 226, 232, 0.52);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 12px 24px rgba(41, 130, 141, 0.08);
}

.ai-result-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.ai-result-head strong {
  display: block;
  color: #173746;
  font-size: 16px;
}

.ai-result-source {
  display: inline-flex;
  margin-top: 6px;
  color: #66a79a;
  font-size: 12px;
  font-weight: 700;
}

.ai-result-head span {
  color: #24a890;
  font-size: 12px;
  font-weight: 700;
}

.ai-result-card p {
  margin: 10px 0 0;
  color: #5f7581;
  line-height: 1.75;
  min-height: 74px;
}

.toolbar-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-filters.secondary {
  margin-top: 14px;
}

.filter-chip {
  height: 40px;
  padding: 0 18px;
  border: 1px solid rgba(188, 222, 227, 0.9);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.72);
  color: #5f7581;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.22s ease;
}

.filter-chip.active,
.filter-chip:hover {
  border-color: rgba(76, 198, 176, 0.55);
  background: linear-gradient(135deg, rgba(78, 216, 174, 0.16), rgba(105, 184, 255, 0.16));
  color: #205262;
  box-shadow: 0 10px 18px rgba(56, 167, 169, 0.1);
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
  margin-top: 18px;
}

.resource-card {
  display: flex;
  flex-direction: column;
  padding: 22px;
  border: 1px solid rgba(188, 222, 227, 0.52);
  border-radius: 28px;
  background: rgba(247, 253, 251, 0.8);
  box-shadow: 0 16px 34px rgba(35, 72, 78, 0.08);
  backdrop-filter: blur(10px);
  transition: all 0.22s ease;
}

.resource-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 18px 30px rgba(35, 72, 78, 0.1);
}

.resource-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.resource-source {
  display: inline-flex;
  margin-bottom: 10px;
  color: #57bea9;
  font-size: 13px;
  font-weight: 700;
}

.resource-head h3 {
  margin: 0;
  color: #203648;
  font-size: 24px;
  line-height: 1.25;
}

.resource-type {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 78px;
  height: 32px;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.resource-type.official {
  background: rgba(219, 248, 232, 0.85);
  color: #2e9d72;
}

.resource-type.video {
  background: rgba(223, 238, 255, 0.86);
  color: #4488d8;
}

.resource-desc {
  min-height: 78px;
  margin: 14px 0 0;
  color: #657c87;
  line-height: 1.85;
}

.resource-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}

.resource-meta span,
.resource-tags span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  background: rgba(243, 250, 252, 0.95);
  color: #607783;
  font-size: 12px;
  font-weight: 600;
}

.resource-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.resource-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 46px;
  margin-top: 18px;
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, #5cd3a9, #57a0f4);
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
}

.resource-link--inline {
  width: auto;
  min-width: 120px;
  height: 38px;
}

@media (max-width: 1360px) {
  .resource-grid,
  .ai-result-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 1080px) {
  .hero-panel,
  .ai-recommend-head {
    flex-direction: column;
  }

  .hero-stats--compact,
  .resource-grid,
  .ai-result-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .online-course-page {
    padding-top: 0;
  }

  .hero-panel,
  .toolbar-panel,
  .ai-recommend-panel,
  .resource-card {
    padding: 20px;
    border-radius: 24px;
  }

  .hero-copy h1 {
    font-size: 34px;
  }

  .hero-stats--compact,
  .resource-grid,
  .ai-result-grid {
    grid-template-columns: 1fr;
  }

  .resource-head {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
