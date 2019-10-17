//app.js
import {
  getLoginInfo, getUserAddrs
} from './utils/api'
import {
  getCurrentAddress, getUserInfo, alert, connectWebsocket,
  coordFormat, fetch, confirm
} from './utils/util'
import {
  gcj02tobd09
} from './utils/coordtransform'
import distance from './utils/distance'
App({
  onLaunch: function () {

  },

  onShow(){

  },

  getLoginInfo: function (cb) {
    console.log("app get login")
    var that = this
    if (this.globalData.loginInfo.haslogin)
    {
      cb && cb(this.globalData.loginInfo)
    }
    else
    {
      wx.getSetting({
        success: (res) => {
          if (res.authSetting['scope.userInfo']) {
            getLoginInfo({
              success(data) {
                getApp().setLoginInfo(data)
                cb && cb(data)
              },
              error(res) {
                console.log("获取用户信息失败:" + JSON.stringify(res))
                cb && cb(res)
              },
              fail(res) {
                if (res.errMsg == 'getUserInfo:fail auth deny' && wx.openSetting) {
                  confirm({
                    content: '若不授用户信息权限, 则无法正常显示用户头像和昵称以及获得相关服务, 请重新授权用户信息权限',
                    cancelText: '不授权',
                    confirmText: '授权',
                    ok() {
                      wx.openSetting({
                        success(res) {
                          if (res.authSetting['scope.userInfo']) {
                            getLoginInfo({
                              success(data) {
                                getApp().setLoginInfo(data)
                                cb && cb(data)
                              }
                            })
                          }
                          else {
                            alert('获取用户信息失败')
                          }
                        }
                      })
                    }
                  })

                } else {
                  console.log(res)
                  alert('获取用户信息失败')
                }
                cb && cb()
              }
            })
          }
          else {
            alert("若不授用户信息权限, 则无法正常显示用户头像和昵称以及获得相关服务, 请重新授权用户信息权限");
          }
        }
      })
    }
  },
  setLoginInfo(loginInfo) {
    console.log("save logininfo:" + JSON.stringify(loginInfo))
    wx.setStorageSync("haslogin", true)
    wx.setStorageSync("userInfo", loginInfo.userInfo)
    this.globalData.userInfo = loginInfo.userInfo
  },


  // 获取当前地址
  getCurrentAddress(cb) {
    var that = this
    if (this.globalData.currentAddress) {
      return cb && cb(this.globalData.currentAddress)
    }

    getCurrentAddress(address => {
      address = that.setCurrentAddress(address)
      cb(address)
    })
  },
  setCurrentAddress(address) {
    if (address.addr_id) {
      address.title = `${address.addr} ${address.detail}`
      address.city = address.city_name
      address.district = address.district_name
      address.location = {
        longitude: address.longitude,
        latitude: address.latitude
      }
    } else {
      address.location = coordFormat(address.location)
    }
    this.globalData.currentAddress = address
    return address
  },

  findNearbyUserAddr(cb, radius = 100) {
    radius /= 100
    wx.getLocation({
      type: 'gcj02',
      success: function (res) {
        var [lng1, lat1] = gcj02tobd09(res.longitude, res.latitude)
        getUserAddrs({
          success(addressList) {
            for (let i = 0, len = addressList.length; i < len; i++) {
              var address = addressList[i]
              var {
                longitude: lng2,
                latitude: lat2
              } = address
              if (distance(lat1, lng1, lat2, lng2) <= radius) {
                return cb(address)
              }
            }
            return cb()
          }
        })
      },
      fail(res) {
        console.log(res.errMsg)
        alert('获取用户地址失败')
      }
    })
  },

  globalData: {
    loginInfo:{
      haslogin: 0,
      userInfo:null
    },
  }
})