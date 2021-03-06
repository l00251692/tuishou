import {
  getRegisterInfo, updateLocation, bindPhone, bindUserFromInfo
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
    phone:'',
    addr_string:'',
    nickName:'',
    disabled:true
  },
  onLoad: function(options) {
    var obj = wx.getLaunchOptionsSync()
    console.log('启动小程序的 query 参数:', JSON.stringify(obj.query))
    if (obj.query) {
      this.setData({
        project_id: obj.query.id,
        from_user_id: obj.query.from_user_id
      })
    }
    else {
      wx.showToast({
        title: '二维码已过期',
      })
      this.setData({
        project_id: '',
        from_user_id: ''
      })
    }
    /*
    if (options.scene) {
      console.log("has scene");
      var scene = decodeURIComponent(options.scene);
      console.log("scene is ", scene);
      var arrPara = scene.split("&");
      var arr = [];
      for (var i in arrPara) {
        arr = arrPara[i].split("=");
        console.log("log:", arr[0], "=", arr[1]);
        if (arr[0] == 'id') {
          this.setData({
            project_id: arr[1]
          })
        }
        if (arr[0] == 'from_user_id')
        {
          this.setData({
            from_user_id: arr[1]
          })
        }
      }
    } else {
      console.log("no scene");
      this.setData({
        project_id: ''
      })
    }*/

  },
  onReady: function() {

  },

  onLogin(e) {
    console.log("to login")
    var that = this
    console.log(e.detail.errMsg)
    if (e.detail.errMsg == 'getUserInfo:ok') {

      wx.showToast({
        title: '登录中...',
        icon:'loading',
        duration:5000,
      })

      getApp().getLoginInfo(loginInfo => {
        var user_id = loginInfo.userInfo.user_id

        if(user_id != null && user_id != ''){
          getRegisterInfo({
            user_id,
            success(data){
              that.setData({
                phone:data.phone,
                addr_string: data.location.city + data.location.district,
                location:data.location,
              })
              if (data.phone != "" && data.location.district != "")
              {
                that.setData({
                  disabled: false
                })
              }
              that.setData({
                nickName: loginInfo.userInfo.nickName,
                haslogin: true
              })
              wx.hideToast()
            },
            error(res){
              that.setData({
                nickName: loginInfo.userInfo.nickName,
                haslogin: true
              })
              wx.hideToast()
            }
          })
        }
        else{
          that.setData({
            nickName: loginInfo.userInfo.nickName,
            haslogin: true
          })
          wx.hideToast()
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

    var that = this
    var nickName = this.data.nickName
    if (nickName == '') {
      return alert("请先授权微信登录")
    }

    var addr_string = this.data.addr_string

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

          if (addr_string != "")
          {
            that.setData({
              disabled: false
            })
          }
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

    var nickName = this.data.nickName
    var phone = this.data.phone
    if (nickName == '') {
      return alert("请先授权微信登录")
    }

    var phone = this.data.phone

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

                if (phone != "") {
                  that.setData({
                    disabled: false
                  })
                }
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

  confirm: function() {
    var that = this;
    var { nickName, phone, addr_string, location, project_id, from_user_id} = this.data

    if (nickName == '') {
      return alert("请先授权获得昵称信息")
    }

    if(phone == '')
    {
      return alert("请先授权绑定手机")
    }

    if (addr_string == '')
    {
      return alert("请选择区域信息")
    }

    updateLocation({
      longitude: location.location.longitude,
      latitude: location.location.latitude,
      province: location.province,
      city: location.city,
      district: location.district,
      name:location.name,
      success(data) {
        //bind user to project and from_user
        bindUserFromInfo({
          from_user_id,
          project_id,
          success(data){
            //do nothing
          },
          error(data){
            //do nothing
          }
        })

        var userInfo = wx.getStorageSync("userInfo")
        userInfo.phone = phone
        userInfo.city = location.city
        userInfo.province = location.province
        wx.setStorageSync("userInfo", userInfo)
        wx.navigateTo({
          url: '/pages/customer/qrPrjInfo?id=' + project_id,
        })
      },
      error(res) {
        wx.showToast({
		      icon: 'loading',
          title: '提交失败',
        })
      }
    })
  }
});