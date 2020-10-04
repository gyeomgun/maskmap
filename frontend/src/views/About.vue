<template>
  <div class="about">
    <h1 style="text-align: center;">공적 마스크 구매 지도</h1>
    <div style="background:#eee;padding: 20px">
        <Card :bordered="false">
            <p slot="title">Notice</p>
            <p>공공API 접근이 원활하지 못하여, 보관된 데이터를 노출합니다</p>
             <p>100개 이상 : 충분 * 녹색</p>
 <p> 100개 미만(99개~30개) : 보통 * 노랑색</p>
 <p> 30개 미만(29개~2개) : 부족 * 빨강색</p>
 <p> 1개~0개 : 없음 또는 판매전 : * 회색</p>
 <p>식약처 공적마스크 구매 안내 : <a href="http://blog.naver.com/kfdazzang/221839489769">이동</a></p>
        </Card>
    </div>
    <Button type="success" size="large" long @click="goMap()">보기</Button>
   <Row style="background:#eee;padding:20px">
        <Col span="11">
            <Card :bordered="false">
                <p slot="title">{{firstDay}}</p>
                <p v-for="(item, idx) in firstDayTarget" :key="idx">{{item}}</p>
            </Card>
        </Col>
        <Col span="11" offset="2">
            <Card shadow>
                <p slot="title">{{secondDay}}</p>
                <p v-for="(item, idx) in secondDayTarget" :key="idx">{{item}}</p>
            </Card>
        </Col>
    </Row>    
    <div class="footer">
      개발자 <a href="mailto:interhg@gmail.com" title="김현겸">gyeomgun (interhg@gmail.com) </a><br/>
      아이콘 제작자 <a href="https://www.flaticon.com/kr/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/kr/" title="Flaticon">www.flaticon.com</a>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      firstDay: null,
      secondDay: null,
      firstDayTarget: [],
      secondDayTarget: []
    }
  },
  methods: {
    goMap () {
      navigator.geolocation.getCurrentPosition((geo) => {
        this.$router.push({ path:"/map", query: { lat:geo.coords.latitude, lon: geo.coords.longitude }})
      }, (err) => {
        alert('위치 정보를 확인 할 수 없습니다')
        this.$router.push({ path:"/map", query: { lat: 0, lon: 0 }})
      }, {
        enableHighAccuracy: true,
        timeout : 5000
      })
      
    }
  },
  created () {
    Date.prototype.addDays = function(days) {
      var date = new Date(this.valueOf())
      date.setDate(date.getDate() + days)
      return date
    }
    const now = new Date()
    const day = now.getDay() //6 sat, 0 sun

    var add = 0
    if (day == 6) {
      add = 2
    }
    if (day == 0) {
      add = 1
    }
    const firstDate = now.addDays(add)
    const secondDate = now.addDays(add + 1)
    for (var i = 0; i < 9; i++) {
      var firstOne = 1930 + (i * 10) + firstDate.getDay()
      var secondOne = 1930 + (i * 10) + secondDate.getDay()
      var strFirst = firstOne + ', ' + (firstOne + 5) + ' 년생'
      var strSecond = secondOne + ', ' + (secondOne + 5) +' 년생'
      this.firstDayTarget.push(strFirst)
      this.secondDayTarget.push(strSecond)
    }

    Date.prototype.format = function(f) {
    if (!this.valueOf()) return " ";
 
    var weekName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
    var d = this;
     
    return f.replace(/(yyyy|yy|MM|dd|E|hh|mm|ss|a\/p)/gi, function($1) {
        switch ($1) {
            case "yyyy": return d.getFullYear();
            case "yy": return (d.getFullYear() % 1000).zf(2);
            case "MM": return (d.getMonth() + 1).zf(2);
            case "dd": return d.getDate().zf(2);
            case "E": return weekName[d.getDay()];
            case "HH": return d.getHours().zf(2);
            case "hh": return ((h = d.getHours() % 12) ? h : 12).zf(2);
            case "mm": return d.getMinutes().zf(2);
            case "ss": return d.getSeconds().zf(2);
            case "a/p": return d.getHours() < 12 ? "오전" : "오후";
            default: return $1;
        }
      });
    };
 
    String.prototype.string = function(len){var s = '', i = 0; while (i++ < len) { s += this; } return s;};
    String.prototype.zf = function(len){return "0".string(len - this.length) + this;};
    Number.prototype.zf = function(len){return this.toString().zf(len);};

    this.firstDay = firstDate.format("MM/dd E")
    this.secondDay = secondDate.format("MM/dd E")
  }
}
</script>
<style scoped>
.footer { position: fixed; bottom: 0; width: 100%; }
</style>