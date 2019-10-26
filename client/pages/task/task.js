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
    /*
    task_list:[
      { task_id: 1, task_auth: '/images/tmp/user.png', task_name: 'ETC推广服务', region: '全国', task_head: '/images/tmp/task1.jpg', task_detail: 'ETC推广，方便，给用户最大实惠', remain_days: 5},
      { task_id: 2, task_auth: '/images/tmp/user.png', task_name: 'ETC推广服务', region: '全国', task_head: '/images/tmp/task2.jpg', task_detail: 'ETC推广，方便，给用户最大实惠', remain_days: 5 }
    ]*/
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    
    var that = this
    that.initData()
    that.getProjectList()

    /*
    wx.getLocation({
      type: 'gcj02',
      success(res) {

        var {
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
            that.setData({
              weizhi: Object.assign({
                location
              }, data),
              addr_titile: data.city,
              auth:true
            })
            that.initData()
            that.getProjectList()
            that.updateLocation()
          }
        })

      },
      fail(res) {
        console.log(res.errMsg)
        if (res.errMsg == 'getLocation:fail auth deny') {

          wx.showModal({
            content: "未授权获位置信息将影响相关功能，请点击右上角设置按钮进行授权",
            success(res) {
              wx.switchTab({
                url: '/pages/index/index',
              })
            }
          })

        } else {
          alert('获取用户地址失败')
        }

      }
    })*/
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
    if(this.data.auth){
      console.log("auth ok")
    }
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function() {
    console.log("on pull down")
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function() {

  },

  initData() {
    this.setData({
      page: 0,
      hasMore: true,
      loading: false,
      projectList: null
    })
  },

  getProjectList(cb) {
    if (this.data.loading) {
      return;
    }
    var that = this;
    var {
      page
    } = this.data

    this.setData({
      loading: true
    })
    
    getProjectList({
      page,
      success(data) {
        console.log("get proj list success")
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

  onChooseLocation: function() {
    console.log("onChooseLocation")
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

  }
})