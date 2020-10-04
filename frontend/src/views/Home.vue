<template>
<div style="width:100%;height:100%;" class="mct_map">
  <div class="wrap">
    <naver-maps      
      class="map"
      :height="height"
      :width="width"
      :mapOptions="mapOptions"
      :initLayers="initLayers"
      @load="onLoad">      
    </naver-maps>
    <Button class="myPositionBtn" shape="circle" icon="md-locate" @click="findMyPosition">내 위치</Button>
    <Button class="refreshBtn" shape="circle" icon="md-refresh" @click="refresh">여기서 검색</Button>
    <Button class="listBtn" shape="circle" icon="ios-list-box-outline" @click="isShowHistory = true">목록</Button>
    <div class="searchDiv">
      <AutoComplete
        size="large"
        v-model="keyword"
        @on-search="autocomplete"
        placeholder="검색"
        class="searchBtn">
        <Option v-for="(item, idx) in data2" :value="item.name" :key="idx">
          <span @click="searchByResult(item.position)">{{item.name}} - {{item.addr}}</span>
        </Option>
      </AutoComplete>
    </div>
    <div class="debug">{{this.gpsPos.x}} - {{this.gpsPos.y}}</div>    
  </div>
  <div class="over">
    <swiper :options="swiperOption" @slideChange="triggerClickMarker" ref="mySwiper" class="swiper">
      <swiper-slide v-for="(pharmacy, index) in pharmacyList" :key="index">
        <pharmacy-list-item :item="pharmacy" @clickHistory="openHistoryPannel" @clickRadio="changeState"></pharmacy-list-item>
      </swiper-slide>
    </swiper>
  </div>
  <Drawer title="목록" :closable="true" v-model="isShowHistory">
     <List>
        <ListItem v-for="(item, index) in pharmacyList" :key="index">
          <ListItemMeta :avatar="getAvatar(item.state)" :title="item.name" :description="item.updatedAtString" />
          <template slot="action">
                <li>
                    <a href="" v-on:click.prevent="move(index)">이동</a>
                </li>
          </template>
        </ListItem>
     </List>
  </Drawer>
  <Spin size="large" fix v-if="spinShow"></Spin>
</div>
</template>

<script>
import 'swiper/css/swiper.css'
import { swiper, swiperSlide } from 'vue-awesome-swiper'
import pharmacyListItem from '../components/PharmacyListItem.vue'
import { debounce } from "debounce"

export default {
  name: 'Map',
  components: {
    swiper,
    swiperSlide,
    pharmacyListItem
  },
  data () {
    return {
      data2: [],
      spinShow: false,
      isShowHistory: false,
      swiperOption: {        
          spaceBetween: 30,
          centeredSlides: true,          
      },

      pharmacyList: [],
      defaultPos: {
        x: 127.1150501,
        y: 37.325271
      },
      gpsPos: {
        x: 0,
        y: 0
      },
      circle: null,
      centerMarker: null,
      keyword: '',
      history: [],
      markerList: [],
      width: 800,
      height: 800,
      info: false,
      marker: null,
      count: 1,
      map: null,
      isCTT: false,
      mapOptions: {
        lat: 37.325271,
        lng: 127.1150501,
        zoom: 17,
        zoomControl: false,
        mapTypeControl: false
      },
      initLayers: ['BACKGROUND', 'BACKGROUND_DETAIL', 'POI_KOREAN', 'TRANSIT']
    }
  },
  computed: {

  },
  created () {
    window.addEventListener('resize', this.handleResize)
    this.handleResize()
    const lat = this.$route.query.lat
    const lon = this.$route.query.lon

    if (lat != 0 && lon !=0) {
      this.defaultPos.x = lon
      this.defaultPos.y = lat
    }
  },
  methods: {
    handleResize () {
      this.width = window.innerWidth
      this.height = window.innerHeight
    },
    onLoad (vue) {
      this.map = vue.map
      naver.maps.Event.addListener(this.map, 'zoom_changed',() => {
        var zoom = this.map.getZoom()
        if (zoom <= 16) {
          if (this.circle != null) {
            this.circle.setVisible(true)
          }
        } else {
          if (this.circle != null) {
            this.circle.setVisible(false)
          }
        }
      })
      // naver.maps.Event.addListener(this.map, 'dragend', pointerEvent => {
      //   const center = this.map.getCenter()
      //   this.search(center.x, center.y, false)
      // })
      this.search(this.defaultPos.x, this.defaultPos.y, true);
    },
    markerRefresh (autoFocus) {
      if (this.pharmacyList.length == 0) {
        return
      }
      var tempList = []
      this.markerList.forEach(s => {
        tempList.push(s)        
      })
      this.marketList = []
      this.pharmacyList.forEach((s, idx) => {
        var zIndex
        if (s.state === 'EMPTY') {
          zIndex = 0
        } else if (s.state === 'FEW') {
          zIndex = 1
        } else if (s.state === 'SOME') {
          zIndex = 2
        } else {
          zIndex = 3
        }
        var marker = new naver.maps.Marker({
          position: new naver.maps.LatLng(s.position.y, s.position.x),
          map: this.map,
          icon: this.getAvatar(s.state),
          zIndex: zIndex
        });

        var listener = naver.maps.Event.addListener(marker, 'click', () => {
          this.map.setCenter(marker.getPosition());
          this.$refs.mySwiper.swiper.slideTo(idx)
        });
        this.markerList.push(marker)
      })
      tempList.forEach(s => {
        s.setMap(null)
      })
      if (autoFocus) {

        var point = null
        var index = 0
        this.pharmacyList.forEach((s, idx) => {
          if (s.state !== 'EMPTY') {
            index = idx
            point = new naver.maps.Point(s.position.x, s.position.y)
          }
        })

        if (point == null) {
          var x = this.pharmacyList[0].position.x
          var y = this.pharmacyList[0].position.y
          point = new naver.maps.Point(x, y)
        }
        this.$refs.mySwiper.swiper.slideTo(index)
        this.map.panTo(point)
      }
    },
    onWindowLoad (that) {
    },
    onMarkerLoaded (vue) {
      this.marker = vue.marker
      this.marker.setDraggable(true)
      this.marker.setCursor('')
      this.marker.setClickable(true)
    },
    onMarkerClicked (event){
      console.log(event)
    },
    triggerClickMarker () {
      const idx = this.$refs.mySwiper.swiper.activeIndex
      const pos = this.pharmacyList[idx].position
      this.map.panTo(new naver.maps.Point(pos.x, pos.y))
    },
    search (x, y, autoFocus) {
      if (this.centerMarker != null) {
        this.centerMarker.setMap(null)
      }
      this.centerMarker = new naver.maps.Marker({
        position: new naver.maps.Point(x,y),
        map: this.map,
        clickable: false,
        zIndex: 100
      });

      if (this.circle != null) {
        this.circle.setMap(null)
      }
      this.circle = new naver.maps.Circle({
        strokeColor: '#0000ff',
        strokeOpacity: 0.8,
        strokeWeight: 1,
        fillColor: '#0000ff',
        fillOpacity: 0.1,
        center: new naver.maps.LatLng(y, x),
        radius: 600,
        zIndex: 100,
        map: this.map
      });

      if (this.map.getZoom() > 16) {
        this.circle.setVisible(false)
      }
      this.spinShow = true
      var url = `/map/v1/retrieve?lat=${y}&lon=${x}`
      this.$http.get(url).then(res => {
        if (res.data.code !== '0000') {
          this.$Message.error(res.data.message);
          return
        }
        //기존 마커 삭제
        this.pharmacyList = res.data.data
        this.markerRefresh(autoFocus)
      }).catch( (err) => {

      }).finally(() => {
        this.spinShow = false
      })
    },
    openHistoryPannel (id) {
      this.isShowHistory = true
      var url = `/map/v1/${id}/history`
      this.$http.get(url).then(res => {
        this.history = res.data.data
      }).catch( (err) => {

      })
    },
    getAvatar (state) {
      if (state === 'FEW') {
        return '/static/img/some.png'
      }
      if (state === 'PLENTY') {
        return '/static/img/available.png'
      }
      if (state === 'SOME') {
        return '/static/img/few.png'
      }
      if (state === 'EMPTY') {
        return '/static/img/soldout.png'
      }      
    },
    changeState (param) {
      if (this.gpsPos.x == 0 && this.gpsPos.y == 0) {
        this.$Message.error('현재 위치를 확인 할 수 없습니다');
        return
      }
      var url = `/map/v1/${param.id}/state/${param.state}?lat=${this.gpsPos.y}&lon=${this.gpsPos.x}`
      this.$http.put(url).then(res => {
        
      }).catch((err) => {
        this.$Message.error('100M 밖의 상태는 변경할 수 없습니다');        
      })      
    },    
    findMyPosition () {
      this.keyword = null
      // 변수로 지도 이동
      if (this.gpsPos.x != 0 && this.gpsPos.y != 0) {
        this.search(this.gpsPos.x, this.gpsPos.y, true)
        return
      }
      navigator.geolocation.getCurrentPosition((geo) => {
        this.search(geo.coords.longitude, geo.coords.latitude)
      }, (err) => {
      }, {
        enableHighAccuracy: true,
        timeout : 5000
      })
    },
    refresh () {
      const center = this.map.getCenter()
      this.search(center.x, center.y, false)
    },
    autocomplete: debounce(function(e) {
      if (this.keyword === '' || this.keyword == null) {
        return
      }
      var url = `/map/v1/autocomplete?keyword=${this.keyword}`
      this.$http.get(url).then(res => {
        this.data2 = res.data.data
      }).catch( (err) => {

      }).finally(() => {
        
      })
    }, 1000),
    searchByResult (item) {
      this.search(item.x, item.y, false)
      this.map.setCenter(new naver.maps.Point(item.x, item.y))
    },
    move (index) {
      this.isShowHistory = false      
      this.$refs.mySwiper.swiper.slideTo(index)
    }
  },
  mounted () {
    // 위치 가져오기 (watch) => 변수 갱신
    navigator.geolocation.watchPosition((geo) => {
      this.gpsPos.x = geo.coords.longitude
      this.gpsPos.y = geo.coords.latitude      
    })
  }
}
</script>
<style scoped>

.over { 
  position:absolute; 
  /* top:75%;  */
  bottom: 5%;
  left:0px;
  width:100%; 
  height:160px; 
  background:#FFFFFF; 
}
.warp {
    overflow: hidden;
    position: relative;
    min-width: 320px;
}
.mct_map {
    display: -webkit-box;
    display: -moz-box;
    display: -ms-flexbox;
    display: flex;
    width: 100%;
    height: 100%;
    -webkit-box-orient: vertical;
    -webkit-box-direction: normal;
    -moz-box-orient: vertical;
    -moz-box-direction: normal;
    -ms-flex-direction: column;
    flex-direction: column;
  }
  .swiper {
    margin-top: 10px;
    margin-bottom: 10px;
    margin-left: 10px;
    margin-right: 10px;
  }
  .myPosition {
    position: absolute;
    top: 10px;
    left: 10px;
    width: 32px;
    height: 32px;
    background-color: #FFFFFF;
    background-image: url("/static/img/my.png");
    box-shadow: 1px 1px 1px 1px gray; 
  }
  .myPositionBtn {
    position: absolute;
    top: 60px;
    left: 10px;
    width: 120px;
    /* height: 32px; */
    background-color: #FFFFFF;
    box-shadow: 1px 1px 1px 1px gray; 
  }
  .refreshBtn {
    position: absolute;
    top: 100px;
    left: 10px;
    width: 120px;
    /* height: 32px; */
    background-color: #FFFFFF;
    box-shadow: 1px 1px 1px 1px gray; 
  }
  .listBtn {
    position: absolute;
    top: 60px;
    right: 10px;
    width: 120px;
    /* height: 32px; */
    background-color: #FFFFFF;
    box-shadow: 1px 1px 1px 1px gray;     
  }
  .searchBtn {
    /* margin-left: 5px;
    margin-top: 5px;
    margin-right: 5px;
    margin-bottom: 5px; */
  }
  .searchDiv {
    position: absolute;
    top: 0px;
    left: 0px;
    width: 100%;
    /* height: 50px; */
    background-color: #0000BB;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 5px;
    padding-right: 5px;
  }
  .debug {
    position: absolute;
    display: none;
    top: 10px;
    right: 10px;
    width: 100px;
    height: 100px;
    background-color: #FFFFFF;
  }  
</style>
