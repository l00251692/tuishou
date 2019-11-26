import {
  getProjectList, updateLocation
} from '../../utils/api'
import {
  alert,
  reverseGeocoder,
  getAddressFromLocation
} from '../../utils/util'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    page: 0,
    hasMore: true,
    loading: false,
    addr_titile: '定位中...',
    auth: false,
    task_list: null,
    banner_arr: [
      { banner_id: 1, carousel_img: '/images/task/banner1.jpg' },
      { banner_id: 2, carousel_img: '/images/task/banner2.jpg' },
      { banner_id: 3, carousel_img: '/images/task/banner3.jpg' }
    ],
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    var that = this
    that.initData()
    that.getProjectList()
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function() {
    if (getApp().globalData.task_refresh == true) {
      this.initData()
      this.getProjectList()
      getApp().globalData.task_refresh = false
    }
  },

  initData() {
    this.setData({
      page: 0,
      hasMore: true,
      loading: false,
      task_list: null
    })
  },

  getProjectList(cb) {
    if (this.data.loading) {
      return;
    }
    var that = this;
    var { page } = this.data

    this.setData({
      loading: true
    })
    
    getProjectList({
      page,
      success(data) {
        var list = data.list
        var {
          task_list
        } = that.data
        that.setData({
          task_list: task_list ? task_list.concat(list) : list,
          page: page + 1,
          hasMore: data.count == 10, //一次最多10个，如果这次取到10个说明还有
          loading: false
        })
        cb && cb()
      },
      error(res) {
        that.setData({
          loading: false
        })
      }
    })
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
    this.initData()
    this.getProjectList()
    wx.stopPullDownRefresh()
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    this.getProjectList()
  },

  onChooseLocation: function() {
    var that = this

    wx.authorize({
      scope: 'scope.userLocation',
      success() {
        console.log("wx authorize success")
        wx.chooseLocation({
          success: function(res) {
            var {
              name,
              address,
              longitude,
              latitude
            } = res
            var location = {
              longitude,
              latitude
            }

            reverseGeocoder({
              location,
              success(data) {
                console.log(data)
                that.setData({
                  location: Object.assign({
                    name,
                    address,
                    location
                  }, data),
                  addr_titile: data.city,
                })
                console.log(JSON.stringify(that.data.location))
              }
            })
          },
        })
      },
      fail(res) {
        alert("未授权获位置信息将影响相关功能，请点击右上角设置按钮进行授权")
      }

    })
  },

  updateLocation: function(){
    var weizhi = this.data.weizhi

    updateLocation({
      longitude: weizhi.location.longitude,
      latitude: weizhi.location.latitude,
      province: weizhi.province,
      city: weizhi.city,
      district: weizhi.district,
      success(data){
        console.log("update location success")
      },
      error(res){
        console.log("update location fail")
      }
    })

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function() {
    return {
      title: '推手号',
      path: '/pages/task/task'
    }
  }
})