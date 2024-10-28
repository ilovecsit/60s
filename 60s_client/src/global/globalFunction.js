import {ElMessage} from "element-plus";
import {sharedCookie} from "@/global/globalCookie.js";
import router from "@/router/index.js";

function isPhone() {
  const userAgent = navigator.userAgent || navigator.vendor || window.opera;

  // 根据User Agent判断
  const phoneKeywords = ['iPhone', 'Android', 'Mobile'];
  const isPhoneUA = phoneKeywords.some(keyword => userAgent.indexOf(keyword) > -1);

  // 根据视口宽度判断
  const isSmallViewport = window.innerWidth <= 600;

  // 结合两种方法判断
  return isPhoneUA && isSmallViewport;
}


export const sharedFunction = {

  /**
   * 判断是否为手机
   * @returns {boolean}
   */
   isPhone
}