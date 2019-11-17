import {
  getRegisterInfo, updateLocation, bindPhone
} from '../../utils/api'

import {
  alert,
  reverseGeocoder,
  getAddressFromLocation
} from '../../utils/util'
var app = getApp();
Page({
  data: {
    loading: false,
    haslogin:false,
    phone:'',
    addr_string:'',
  },
  onLoad: function() {
    if (wx.getStorageSync('haslogin')) {
      this.setData({
        haslogin: true,
        userInfo: wx.getStorageSync('userInfo')
      })
      this.getRegisterInfo()
    }
  },
  onReady: function() {

  },

  getRegisterInfo(){
    var that = this
    getRegisterInfo({
      success(data){
        that.setData({
          phone: data.phone,
          location:data.location,
          addr_string: data.location.city + data.location.district
        })
      },
      error(res){
        console.log("get registerinfo fail")
      }
    })
  },

  onLogin(e) {

    var that = this
    if (e.detail.errMsg == 'getUserInfo:ok') {

      getApp().getLoginInfo(loginInfo => {
        console.log("login success" + JSON.stringify(loginInfo))
        if (loginInfo == undefined || loginInfo == null) {
          return alert("登录失败，请稍候")
        }
        else {
          that.setData({
            haslogin: true,
            phone: loginInfo.userInfo.phone,
            addr_string: loginInfo.userInfo.district
          });
          
          if (loginInfo.userInfo.phone != '' && loginInfo.userInfo.district != '')
          {
            wx.navigateBack({})
          }
        }

      })
    }
  },

  bindPhoneNumber: function(e) {
    console.log("bind phone" + JSON.stringify(e))
    if (e.detail.errMsg !== "getPhoneNumber:ok") {
      // 拒绝授权
      return;
    }

    if (this.data.haslogin != true) {
      return alert("请先登录");
    }

    var that = this
    var ency = e.detail.encryptedData;
    var iv = e.detail.iv;
    getApp().getLoginInfo(loginInfo => {
      var {
        session_key
      } = wx.getStorageSync('userInfo');

      bindPhone({
        ency,
        iv,
        session_key,
        success(data) {
          wx.showToast({
            title: '获取手机号成功',
          })
          that.setData({
            phone: data.phone
          })
        },
        error(res) {
          alert("获取手机号失败")
        }
      })
    })

  },

  onChooseLocation: function() {
    console.log("onChooseLocation")
    var that = this

    if (this.data.haslogin != true) {
      return alert("请先登录");
    }

    if (this.data.phone == '') {
      return alert("请先绑定手机号信息");
    }

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
                  addr_string: data.city + data.district,
                })
                console.log(JSON.stringify(that.data.location))
                updateLocation({
                  longitude: location.longitude,
                  latitude: location.latitude,
                  province: data.province,
                  city: data.city,
                  district: data.district,
                  name: name,
                  success(data) {
                    console.log("update location success")
                    var userInfo = wx.getStorageSync("userInfo")
                    console.log("userinf1:" + JSON.stringify(userInfo))
                    userInfo.phone = that.data.phone
                    userInfo.city = location.city
                    userInfo.province = location.province
                    console.log("userinf2:" + JSON.stringify(userInfo))
                    wx.setStorageSync("userInfo", userInfo)
                    wx.showToast({
                      title: '注册成功',
                    })
                    wx.navigateBack({})
                  },
                  error(res) {
                    console.log("update location fail")
                    wx.showToast({
					            icon: 'loading',
                      title: '保存失败',
                    })
                  }
                })
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

});