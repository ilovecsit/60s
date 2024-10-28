<template>
  <div class="canvas-wrap">
    <canvas id="canvas"></canvas>
    <!--    <div class="tools">
          字体：<el-input-number
            v-model="fontSize"
            :min="1"
            :max="50"
            size="small"
        />&nbsp; 速度：<el-input-number
            v-model="speed"
            :min="0"
            :max="1000"
            :step="10"
            size="small"
        />&nbsp; 持久：<el-input-number
            v-model="long"
            :min="0"
            :max="1000"
            :step="10"
            size="small"
        />&nbsp;
          <el-button @click="initCanvas" size="small">刷新</el-button>
        </div>-->
  </div>
</template>

<script setup>
import {onMounted, ref} from "vue";

const [fontSize, speed, long] = [ref(20), ref(50), ref(960)];
const text =
    "床前明月光，疑是地上霜。\n" +
    "举头望明月，低头思故乡。\n" +
    "白日依山尽，黄河入海流。\n" +
    "欲穷千里目，更上一层楼。\n" +
    "红豆生南国，春来发几枝。\n" +
    "君自故乡来，应知故乡事。\n" +
    "海上生明月，天涯共此时。\n" +
    "会当凌绝顶，一览众山小。\n" +
    "但愿人长久，千里共婵娟。\n" +
    "春眠不觉晓，处处闻啼鸟。\n" +
    "野火烧不尽，春风吹又生。\n" +
    "独在异乡为异客，每逢佳节倍思亲。\n" +
    "山重水复疑无路，柳暗花明又一村。\n" +
    "两岸猿声啼不住，轻舟已过万重山。\n" +
    "春色满园关不住，一枝红杏出墙来。\n" +
    "问世间情为何物，直教生死相许。\n" +
    "两情若是久长时，又岂在朝朝暮暮。\n" +
    "人生得意须尽欢，莫使金樽空对月。\n" +
    "天生我材必有用，千金散尽还复来。\n" +
    "月有阴晴圆缺，人有悲欢离合。\n" +
    "但愿君心似我心，定不负相思意。\n" +
    "采得百花成蜜后，为谁辛苦为谁甜。\n" +
    "洛阳亲友如相问，一片冰心在玉壶。\n" +
    "青青子衿，悠悠我心。\n" +
    "窈窕淑女，君子好逑。\n" +
    "关关雎鸠，在河之洲。\n" +
    "蒹葭苍苍，白露为霜。\n" +
    "所谓伊人，在水一方。\n" +
    "执子之手，与子偕老。\n" +
    "路漫漫其修远兮，吾将上下而求索。\n" +
    "莫等闲，白了少年头，空悲切。\n" +
    "此情可待成追忆，只是当时已惘然。\n" +
    "山无陵，江水为竭，冬雷震震，夏雨雪，天地合，乃敢与君绝。\n" +
    "花径不曾缘客扫，蓬门今始为君开。\n" +
    "江畔何人初见月，江月何年初照人。\n" +
    "露从今夜白，月是故乡明。\n" +
    "春风得意马蹄疾，一日看尽长安花。\n" +
    "落红不是无情物，化作春泥更护花。\n" +
    "我本将心向明月，奈何明月照沟渠。\n" +
    "江南好，风景旧曾谙。日出江花红胜火，春来江水绿如蓝。\n" +
    "青青河畔草，郁郁园中柳。\n" +
    "天长地久有时尽，此恨绵绵无绝期。\n" +
    "桃花潭水深千尺，不及汪伦送我情。\n" +
    "君问归期未有期，巴山夜雨涨秋池。\n" +
    "花自飘零水自流，一种相思，两处闲愁。\n" +
    "海上生明月，天涯共此时。\n" +
    "天净沙，秋思。枯藤老树昏鸦，小桥流水人家，古道西风瘦马。\n".split("");

const colors = [
  "rgba(0, 255, 0, 1)",
  "rgba(90,197,232,2)",
  "rgba(196,21,47,1)",
  "rgba(140,139,127,0.91)",
  "rgb(173,157,42)",
]

const randomColor=Math.floor(Math.random() * colors.length);

const initCanvas = () => {
  const wrap = document.querySelector(".canvas-wrap");
  const arr = new Array(Math.ceil(wrap.clientWidth / fontSize.value)).fill(0);

  const canvas = document.querySelector("#canvas");
  canvas.width = wrap.clientWidth;
  canvas.height = wrap.clientHeight;
  const ctx = canvas.getContext("2d");
  ctx.clearRect(0, 0, canvas.clientWidth, canvas.clientHeight);

  const indexRecord = new Array(arr.length).fill(0);
  indexRecord.forEach((item, index) => {
    indexRecord[index] = Math.floor(Math.random() * text.length);
    // console.log(item);
  });

  const initRain = () => {
    ctx.fillStyle = "rgba(0, 0, 0, 0.05)"; // 填充背景颜色
    ctx.fillRect(0, 0, canvas.clientWidth, canvas.clientHeight); // 背景

    ctx.fillStyle = colors[randomColor]; // 文字颜色
    ctx.font = `${fontSize.value}px Arial`;
    arr.forEach((item, index) => {
      ctx.fillText(
          text[indexRecord[index] % text.length],
          index * fontSize.value,
          item + fontSize.value
      );
      indexRecord[index]++;
      arr[index] =
          item > canvas.clientHeight || Math.random() * 1000 > long.value
              ? 0
              : item + fontSize.value;
    });
    speed.value > 0
        ? setTimeout(initRain, speed.value)
        : requestAnimationFrame(initRain);
  };
  initRain();
};


onMounted(() => {
  initCanvas();
});

</script>

<script>
export let TextRain = document.getElementsByTagName("canvas");
</script>




<style lang="scss">
.canvas-wrap {
  width: 100vw;
  height: calc(100vh - 135px);
  border: 1px solid silver;
  box-sizing: border-box;
  font-size: 1px;

  .tools {
    padding: 5px 10px;
    margin-top: 5px;
    border: 1px solid silver;
  }
}
</style>