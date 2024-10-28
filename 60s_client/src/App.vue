<template>
    <!-- 路由出口，用于渲染组件 -->
    <router-view>
    </router-view>
</template>

<script>
export default {
  name: 'App',
};
</script>

<style>

html, body, #app {
  width: 100%;
  height: 100%;
  overflow: hidden;
  margin: 0;
  padding: 0;

}

/* 确保body的默认边距为0 */
body {
  margin: 0;
}

/* 居中显示router-view的内容，如果它没有占满整个视口 */
#app {
  min-width: 100%;
  max-width: 100%;
  min-height: 100%;
  max-height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

</style>