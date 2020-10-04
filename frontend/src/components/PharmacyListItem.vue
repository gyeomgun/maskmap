<template>
  <div style="height: 100%; width: 100%; min-height: 100px; background-color: #FFFFFF">
    <span class="name" v-text="this.defultIfNull(item.name)"></span>
    <span class="address">{{this.defultIfNull(item.addr)}} {{item.mapImg}}</span>
    <!-- <span class="mapImg ivu-icon ivu-icon-ios-compass-outline" v-text="this.defultIfNull(item.mapImg)"></span> -->
    <!-- <span class="info ivu-icon ivu-icon-ios-information-circle-outline" v-text="this.defultIfNull(item.info)"></span> -->
    <span class="tel ivu-icon ivu-icon-ios-call-outline" v-text="this.defultIfNull(item.tel)"></span>
    <div class="stock" v-bind:style="{background: getColor()}" v-html="getStock()"></div>
    <Row type="flex" justify="space-between" align="middle" class="code-row-bg">
      <Col><Tag color="success">갱신시간 {{this.item.updatedAtString}}</Tag></Col>
      <Col><Button type="info" @click="viewInfo">영업시간정보</Button></Col>
    </Row>
    <Modal v-model="info" width="360">
        <p slot="header" style="color:#f60;text-align:center">
            <Icon type="ios-information-circle"></Icon>
            <span>영업 시간 정보</span>
            <p v-for="(item, index) in this.item.operInfo" v-bind:key="index">{{item}}</p>
            <br />
            <span>마스크 입고시간 {{this.item.stockAtString}}</span>
            
        </p>
        <div slot="footer">
          <Button size="large" type="primary" long  @click="info = false">닫기</Button>
        </div>
    </Modal>
  </div>  
</template>
<script>
export default {
  name: 'pharmacyListItem',
  props: ['item', 'gpsPos'],
  data () {
    return {
      info: false
    }
  },
  created () {
  },
  methods: {
    defultIfNull(val) {
      if (val == null) {
        return ''
      }
      return val
    },
    showHistory () {
      this.$emit('clickHistory', this.item.id)
    },
    changeState (state) {
      var param = {}
      param.id = this.item.id
      param.state = state
      this.$emit('clickRadio', param)
    },
    getStock () {
      if (this.item.state === 'FEW') {
        return '30⬇'
      }
      if (this.item.state === 'PLENTY') {
        return '100⬆'
      }
      if (this.item.state == 'SOME') {
        return "100⬇"
      }
      if (this.item.state === 'EMPTY') {
        return '없음'
      }   
    },
    getColor () {
      if (this.item.state === 'FEW') {
        return '#EE0000'
      }
      if (this.item.state === 'PLENTY') {
        return '#00EE00'
      }
      if (this.item.state == 'SOME') {
        return 'yellow'
      }
      if (this.item.state === 'EMPTY') {
        return '#AAAAAA'
      } 
    },
    viewInfo () {
      this.info = true
    }
  }
}
</script>
<style scoped>
  .name {
    font-size: 2em;
    font-weight: bold;
    display: block;
  }
  .address {
    text-align: left;
    display: block;
    line-height:1.5em;
    min-height:3.0em;
  }
  .mapImg {
    padding-top: 2px;
    text-align: left;
    display: block;
    line-height: 1.6em;
  }
  .info {
    padding-top: 2px;
    text-align: left;
    display: block;    
    line-height: 1.6em;    
  }
  .tel {
    padding-top: 2px;
    text-align: left;
    display: block;
    line-height: 1.6em;    
  }
  .stock {
    position: absolute;
    top: 0px;
    /* left: 60%; */
    right: 5px;
    font-size: 2em;
    background: #AAAAAA;
    text-align: center;    
    width: 100px;
    border-radius: 15px;
    color: #FFFFFF;
    font-weight: bold;
    vertical-align: middle;
  }
  .footer { position: fixed; bottom: 0; width: 100%; }
</style>