<template>
  <div id="userLayout" :class="['user-layout-wrapper', device]">
    <vue-particles
      color="#eee"
      :particleOpacity="0.7"
      :particlesNumber="60"
      shapeType="circle"
      :particleSize="4"
      linesColor="#eee"
      :linesWidth="1"
      :lineLinked="true"
      :lineOpacity="0.4"
      :linesDistance="150"
      :moveSpeed="2"
      :hoverEffect="true"
      hoverMode="grab"
      :clickEffect="true"
      clickMode="push"
      class="lizi"
    ></vue-particles>
    <div class="container">
      <div class="login-form">
        <div class="top">
          <div class="header">
            <a href="/">
              <!-- <img src="~@/assets/logo.svg" class="logo" alt="logo"> -->
              <span class="title">小蓝零工管理后台</span>
            </a>
          </div>
          <div class="desc">Management System</div>
        </div>
        <route-view></route-view>
      </div>
      <div class="footer">
        <!-- <div class="links">
          <a href="http://www.baidu.com" target="_blank">帮助</a>
          <a href="" target="_blank">隐私</a>
          <a href="" target="_blank">条款</a>
        </div> -->
        <div class="copyright">
          Copyright &copy; 2023
          <a href="https://www.baidu.com/" target="_blank">小蓝零工</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import RouteView from '@/components/layouts/RouteView';
import { mixinDevice } from '@/utils/mixin.js';

export default {
  name: 'UserLayout',
  components: { RouteView },
  mixins: [mixinDevice],
  data() {
    return {};
  },
  mounted() {
    document.body.classList.add('userLayout');
  },
  beforeDestroy() {
    document.body.classList.remove('userLayout');
  }
};
</script>

<style lang="less" scoped>
.lizi {
  position: absolute;
  width: 100%;
  height: 100%;
  background: #2f4050;
  overflow: hidden;
}

#userLayout.user-layout-wrapper {
  height: 100%;

  &.mobile {
    .container {
      .main {
        max-width: 368px;
        width: 98%;
      }
    }
  }

  .container {
    width: 100%;
    min-height: 100%;
    // background: #f0f2f5 url(~@/assets/background.svg) no-repeat 50%;
    // background-size: 100%;
    // padding: 120px 0 144px;
    position: relative;
    display: flex;

    .login-form {
      background-color: #fff;
      width: 400px;
      margin: auto;
      padding: 10px;
      border-radius: 10px;
    }

    a {
      text-decoration: none;
    }

    .top {
      text-align: center;

      .header {
        // height: 44px;
        // line-height: 44px;

        .badge {
          position: absolute;
          display: inline-block;
          line-height: 1;
          vertical-align: middle;
          margin-left: -12px;
          margin-top: -10px;
          opacity: 0.8;
        }

        .logo {
          height: 44px;
          vertical-align: top;
          margin-right: 16px;
          border-style: none;
        }

        .title {
          font-size: 20px;
          color: rgba(0, 0, 0, 0.8);
          font-family: 'Chinese Quote', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial,
            sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol';
          font-weight: bold;
          position: relative;
          top: 10px;
        }
      }
      .desc {
        font-size: 14px;
        color: rgba(0, 0, 0, 0.6);
        margin-top: 12px;
        margin-bottom: 10px;
        font-weight: bold;
      }
    }

    .main {
      min-width: 260px;
      width: 318px;
      margin: 0 auto;
    }

    .footer {
      position: absolute;
      width: 100%;
      bottom: 0;
      padding: 0 16px;
      margin: 48px 0 24px;
      text-align: center;

      .links {
        margin-bottom: 8px;
        font-size: 14px;
        a {
          color: rgba(0, 0, 0, 0.45);
          transition: all 0.3s;
          &:not(:last-child) {
            margin-right: 40px;
          }
        }
      }
      .copyright {
        color: white;
        font-size: 14px;
      }
    }
  }
}
</style>
