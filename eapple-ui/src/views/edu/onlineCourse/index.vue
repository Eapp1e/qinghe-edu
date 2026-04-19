<template>
  <div class="app-container online-course-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-badge">Online Learning</span>
        <h1>网课中心</h1>
        <p>整合适合中小学阶段的免费学习平台、科普网站与高质量视频入口，帮助学生在课后延伸学习、查漏补缺与拓展兴趣。</p>
      </div>
      <div class="hero-stats">
        <div class="stat-card">
          <span>精选入口</span>
          <strong>{{ filteredResources.length }}</strong>
        </div>
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
          v-for="item in sourceOptions"
          :key="item.value"
          type="button"
          :class="['filter-chip', { active: activeSource === item.value }]"
          @click="activeSource = item.value"
        >
          {{ item.label }}
        </button>
      </div>
    </section>

    <section class="spotlight-grid">
      <article class="spotlight-card primary">
        <div class="spotlight-head">
          <span class="spotlight-kicker">官方优先</span>
          <h3>优先推荐平台</h3>
        </div>
        <div class="spotlight-list">
          <button
            v-for="item in featuredResources"
            :key="item.title"
            type="button"
            class="spotlight-item"
            @click="openResource(item)"
          >
            <div>
              <strong>{{ item.title }}</strong>
              <p>{{ item.description }}</p>
            </div>
            <span>{{ item.source }}</span>
          </button>
        </div>
      </article>

      <article class="spotlight-card tips">
        <div class="spotlight-head">
          <span class="spotlight-kicker">课后建议</span>
          <h3>怎么用更高效</h3>
        </div>
        <ul class="tips-list">
          <li>先用官方平台补课堂知识，再用视频资源做兴趣拓展。</li>
          <li>每次学习控制在 20 到 30 分钟，完成后回到平台记录学习收获。</li>
          <li>遇到看不懂的内容，可以再到作业问答里提问，形成闭环学习。</li>
        </ul>
      </article>
    </section>

    <section class="resource-grid">
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
export default {
  name: 'EduOnlineCourse',
  data() {
    return {
      activeStage: 'all',
      activeSource: 'all',
      stageOptions: [
        { label: '全部阶段', value: 'all' },
        { label: '小学', value: 'primary' },
        { label: '初中', value: 'middle' },
        { label: '通用拓展', value: 'common' }
      ],
      sourceOptions: [
        { label: '全部来源', value: 'all' },
        { label: '官方平台', value: 'official' },
        { label: '视频学习', value: 'video' }
      ],
      resources: [
        {
          title: '国家中小学智慧教育平台',
          source: '教育部平台',
          description: '覆盖小学、初中到高中多学段资源，适合课后同步复习、专题拓展和自主学习。',
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
          title: '中国科普博览',
          source: '中科院科普',
          description: '适合拓展科学兴趣，包含视频、虚拟博物馆、公开课和大量面向青少年的科普内容。',
          category: '科学素养',
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
          title: 'Bilibili 少儿编程启蒙',
          source: 'Bilibili',
          description: '适合对编程感兴趣的学生，能从图形化编程、Scratch 到基础逻辑逐步入门。',
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
        }
      ]
    }
  },
  computed: {
    filteredResources() {
      return this.resources.filter(item => {
        const stageMatched = this.activeStage === 'all' || item.stage === this.activeStage || item.stage === 'common'
        const sourceMatched = this.activeSource === 'all' || item.sourceType === this.activeSource
        return stageMatched && sourceMatched
      })
    },
    featuredResources() {
      return this.resources.filter(item => ['国家中小学智慧教育平台', '中国科普博览', 'Bilibili 少儿编程启蒙'].includes(item.title))
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
      window.open(item.link, '_blank', 'noopener')
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
  display: grid;
  grid-template-columns: minmax(0, 1.8fr) minmax(280px, 1fr);
  gap: 18px;
  padding: 28px 30px;
  border: 1px solid rgba(176, 214, 220, 0.5);
  border-radius: 30px;
  background:
    radial-gradient(circle at top right, rgba(63, 229, 190, 0.18), transparent 28%),
    radial-gradient(circle at bottom left, rgba(72, 153, 255, 0.14), transparent 24%),
    linear-gradient(135deg, rgba(28, 47, 63, 0.96), rgba(49, 82, 92, 0.92));
  box-shadow: 0 24px 54px rgba(23, 42, 52, 0.18);
}

.hero-copy h1 {
  margin: 14px 0 12px;
  color: #fff;
  font-size: 42px;
  line-height: 1.05;
}

.hero-copy p {
  max-width: 740px;
  margin: 0;
  color: rgba(233, 245, 246, 0.88);
  font-size: 15px;
  line-height: 1.9;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 138px;
  height: 42px;
  padding: 0 18px;
  border-radius: 999px;
  background: linear-gradient(135deg, rgba(207, 252, 232, 0.22), rgba(119, 231, 215, 0.15));
  color: #aef4e1;
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 0.03em;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  align-self: stretch;
}

.stat-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 138px;
  padding: 24px 22px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 24px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.06);
}

.stat-card span {
  color: rgba(234, 243, 244, 0.76);
  font-size: 14px;
}

.stat-card strong {
  color: #fff;
  font-size: 36px;
  line-height: 1;
}

.toolbar-panel {
  margin-top: 18px;
  padding: 18px 22px;
  border: 1px solid rgba(191, 223, 228, 0.5);
  border-radius: 26px;
  background: rgba(246, 253, 250, 0.72);
  box-shadow: 0 14px 28px rgba(34, 72, 79, 0.07);
  backdrop-filter: blur(10px);
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

.spotlight-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.45fr) minmax(320px, 0.9fr);
  gap: 18px;
  margin-top: 18px;
}

.spotlight-card,
.resource-card {
  border: 1px solid rgba(188, 222, 227, 0.52);
  border-radius: 28px;
  background: rgba(247, 253, 251, 0.8);
  box-shadow: 0 16px 34px rgba(35, 72, 78, 0.08);
  backdrop-filter: blur(10px);
}

.spotlight-card {
  padding: 24px;
}

.spotlight-card.primary {
  background:
    radial-gradient(circle at bottom right, rgba(62, 229, 189, 0.1), transparent 28%),
    rgba(247, 253, 251, 0.84);
}

.spotlight-head {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 18px;
}

.spotlight-kicker {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  height: 34px;
  padding: 0 14px;
  border-radius: 999px;
  background: rgba(213, 252, 235, 0.7);
  color: #5fc3a8;
  font-size: 13px;
  font-weight: 700;
}

.spotlight-head h3 {
  margin: 0;
  color: #203648;
  font-size: 30px;
}

.spotlight-list {
  display: grid;
  gap: 14px;
}

.spotlight-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  width: 100%;
  padding: 18px 20px;
  border: 1px solid rgba(188, 222, 227, 0.48);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.68);
  text-align: left;
  cursor: pointer;
  transition: all 0.22s ease;
}

.spotlight-item strong,
.resource-head h3 {
  color: #203648;
}

.spotlight-item p {
  margin: 8px 0 0;
  color: #6f8792;
  line-height: 1.75;
}

.spotlight-item span:last-child {
  color: #48a9dd;
  font-size: 13px;
  font-weight: 700;
  white-space: nowrap;
}

.spotlight-item:hover,
.resource-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 18px 30px rgba(35, 72, 78, 0.1);
}

.tips-list {
  display: grid;
  gap: 14px;
  margin: 0;
  padding-left: 18px;
  color: #5e7480;
  line-height: 1.9;
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

@media (max-width: 1360px) {
  .resource-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 1080px) {
  .hero-panel,
  .spotlight-grid {
    grid-template-columns: 1fr;
  }

  .hero-stats {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .online-course-page {
    padding-top: 0;
  }

  .hero-panel,
  .toolbar-panel,
  .spotlight-card,
  .resource-card {
    padding: 20px;
    border-radius: 24px;
  }

  .hero-copy h1 {
    font-size: 34px;
  }

  .hero-stats,
  .resource-grid {
    grid-template-columns: 1fr;
  }

  .spotlight-item,
  .resource-head {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
