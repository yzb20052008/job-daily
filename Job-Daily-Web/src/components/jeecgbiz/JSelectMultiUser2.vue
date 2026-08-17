<template>
  <!-- 定义在这里的参数都是不可在外部覆盖的，防止出现问题 -->
  <j-select-biz-component
    :value="value"
    :ellipsisLength="25"
    :listUrl="url.list"
    :columns="columns"
    v-on="$listeners"
    v-bind="attrs"
  />
</template>

<script>
  import JDate from '@comp/jeecg/JDate'
  import JSelectBizComponent from './JSelectBizComponent'

  export default {
    name: 'JSelectMultiUser',
    components: {JDate, JSelectBizComponent},
    props: {
      value: null, // any type
      queryConfig: {
        type: Array,
        default: () => []
      },
    },
    data() {
      return {
        url: { list: '/sys/user/list?identity=3' },
        columns: [
          { title: '用户昵称', align: 'center', dataIndex: 'nickname' },
          // { title: '账号', align: 'center', width: '25%', dataIndex: 'username' },
          { title: '手机号码', align: 'center', dataIndex: 'phone' },
          { title: '添加时间', align: 'center', dataIndex: 'createTime' }
        ],
        // 定义在这里的参数都是可以在外部传递覆盖的，可以更灵活的定制化使用的组件
        default: {
          name: '用户',
          width: 1200,
          displayKey: 'nickname',
          returnKeys: ['id', 'nickname'],
          queryParamText: '手机号',
          queryParamCode:'phone'
        },
        // 多条件查询配置
        queryConfigDefault: [
          {
            key: 'nickname',
            label: '用户昵称',
          },
        ],
      }
    },
    computed: {
      attrs() {
        return Object.assign(this.default, this.$attrs, {
          queryConfig: this.queryConfigDefault.concat(this.queryConfig)
        })
      }
    }
  }
</script>

<style lang="less" scoped></style>