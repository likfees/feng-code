<template>
  <div class="body" style="margin-top: 24px">
    <page-header-wrapper :breadcrumb="{}">
      <template v-slot:content>
        <div class="page-header-content">
          <div class="avatar">
            <a-avatar size="large" :src="avatar"/>
          </div>
          <div class="content">
            <div class="content-title">
              {{ timeFix }}，{{ nickname }}<span class="welcome-text"></span>
            </div>
            <div v-if="Object.keys(roleGroup).length > 0">{{ roleGroup }}</div>
          </div>
        </div>
      </template>
      <template v-slot:extraContent>
      </template>
      <!-- 致谢项目 -->
      <div>
        <a-row :gutter="24">
          <a-col :xl="14" :lg="24" :md="24" :sm="24" :xs="24">
            <!-- 项目简介 -->
            <a-card
              style="width:100%; margin-bottom: 24px; font-size: 17px"
              title="FengCode 简介"
            >
              <p><b>FengCode</b> 在线编程实践平台</p>
              <p>FengCode是基于SpringBoot，Spring Security，JWT，Vue 的前后端分离权限管理系统。</p>
              <p>
                拥有用户管理、菜单管理、角色管理、字典管理、参数管理、操作日志、登录日志、
                在线用户、定时任务、、系统接口、服务监控、在线构建器、连接池监视等功能。
              </p>
            </a-card>
            <!-- Gitee卡片 -->
            <a-card hoverable style="margin-bottom: 24px">
              <a href="https://gitee.com/lzscxb/feng-code" target="_blank" slot="cover">
                <img style="width:100%" src="https://gitee.com/lzscxb/feng-code/widgets/widget_card.svg?colors=4183c4,ffffff,ffffff,e3e9ed,666666,9b9b9b" >
              </a>
            </a-card>
          </a-col>
          <a-col
            style="padding: 0 12px"
            :xl="10"
            :lg="24"
            :md="24"
            :sm="24"
            :xs="24">
            <!-- Github卡片 -->
            <a-card hoverable style="margin-bottom: 24px">
              <a href="https://github.com/likfees/feng-code" target="_blank" slot="cover">
                <img style="width:100%" src="https://github-readme-stats.vercel.app/api/?username=likfees&repo=feng-code&show_icons=true" >
              </a>
            </a-card>
            <a-card hoverable style="margin-bottom: 24px">
              <a href="https://github.com/likfees/feng-code" target="_blank" slot="cover">
                <img style="width:100%" src="https://github-readme-stats.vercel.app/api/pin/?username=likfees&repo=feng-code&show_owner=true" >
              </a>
            </a-card>
          </a-col>
        </a-row>
      </div>
    </page-header-wrapper>
  </div>
</template>

<script>
import { timeFix } from '@/utils/util'
import { mapGetters } from 'vuex'
import { PageHeaderWrapper } from '@/components/ProLayout'
import { getUserProfile } from '@/api/system/user'
import storage from 'store'
import { USER_AVATAR } from '@/store/mutation-types'

export default {
  name: 'Index',
  components: {
    PageHeaderWrapper
  },
  data () {
    return {
      timeFix: timeFix(),
      // 用户信息
      user: {
      },
      roleGroup: {}
    }
  },
  computed: {
    ...mapGetters([
      'avatar',
      'nickname'
    ])
  },
  created () {
    this.getUser()
  },
  mounted () {
  },
  methods: {
    // 获取用户信息
    getUser () {
      getUserProfile().then(response => {
        this.user = response.data
        this.roleGroup = response.roleGroup
        storage.set(USER_AVATAR, this.user.avatar)
      })
    },
    onSponsorTabChange (key, type) {
      this[type] = key
    }
  }
}
</script>

<style lang="less">
  @import "./index.less";
  .body {
    padding: 0 10px;
    margin: 0 auto;
    max-width: 1375px;
    min-width: 600px;
    height: 100%;
  }
  .ant-pro-page-header-wrap-children-content {
    margin: 24px 0 0 !important;
  }
  blockquote {
    padding: 0 1em;
    color: #6a737d;
    border-left: 0.25em solid #dfe2e5;
  }
  .project-list {

    .card-title {
      font-size: 0;

      a {
        color: rgba(0, 0, 0, 0.85);
        margin-left: 12px;
        line-height: 24px;
        height: 24px;
        display: inline-block;
        vertical-align: top;
        font-size: 14px;

        &:hover {
          color: #1890ff;
        }
      }
    }

    .card-description {
      color: rgba(0, 0, 0, 0.45);
      height: 66px;
      line-height: 22px;
      overflow: hidden;
    }

    .project-item {
      display: flex;
      margin-top: 8px;
      overflow: hidden;
      font-size: 12px;
      height: 20px;
      line-height: 20px;

      a {
        color: rgba(0, 0, 0, 0.45);
        display: inline-block;
        flex: 1 1 0;

        &:hover {
          color: #1890ff;
        }
      }

      .download {
        color: rgba(0, 0, 0, 0.25);
        flex: 0 0 auto;
        float: right;
      }
    }

    .ant-card-meta-description {
      color: rgba(0, 0, 0, 0.45);
      height: 44px;
      line-height: 22px;
      overflow: hidden;
    }
  }

  .item-group {
    padding: 20px 0 8px 24px;
    font-size: 0;

    a {
      color: rgba(0, 0, 0, 0.65);
      display: inline-block;
      font-size: 14px;
      margin-bottom: 13px;
      width: 25%;
    }
  }

  .mobile {

    .project-list {

      .project-card-grid {
        width: 100%;
      }
    }

    .more-info {
      border: 0;
      padding-top: 16px;
      margin: 16px 0 16px;
    }

    .headerContent .title .welcome-text {
      display: none;
    }
  }

</style>
