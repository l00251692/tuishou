import { getProjectInfo
} from '../../utils/api'

Page({
  data: {
    info: {},
    hasMore: true,
    loading: false,
    page: 0,
  },

  onLoad(options) {
    this.callback = options.callback || 'callback'
    this.id = options.id 
    this.loadData() 
  },

  onReady() {
  },
  

  loadData() {
    var that = this
    var id = this.id;
    console.log("loadData:" + id)
    var loading = this.data.loading
    if (loading) {
      return
    }

    this.setData({
      loading: true
    })

    getProjectInfo(
      {
        project_id: id,
        success(data) {
          console.log(JSON.stringify(data))
          that.setData({
            info: data,
            loading: false
          })
        },
        error(res) {
          console.log("fail:" + JSON.stringify(res))
          that.setData({
            loading: false
          })
        }
      }
    )
  },

  onIndex: function (e) {
    wx.switchTab({
      url: '/pages/index/index',
    })
  },

  onTask: function (e) {
    wx.navigateTo({
      url: '/pages/task/detail?id=' + this.id,
    })
  }
});
