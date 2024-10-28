export const sharedCookie = {
    /**
     * 设置cookie
     * @param {string} name - cookie名称
     * @param {number} value - cookie值
     * @param {number} [minutes] - cookie有效时间（分钟）
     */
    set(name, value, minutes) {
        // 如果cookie不为空,删除cookie
        let expires = "";
        if (minutes) {
            var date = new Date();
            date.setTime(date.getTime() + (minutes * 60 * 1000));
            expires = "; expires=" + date.toUTCString();
        }
        document.cookie = name + "=" + (value || "") + expires + "; path=/";
    },

    /**
     * 获取cookie
     * @param {string} name - cookie名称
     * @return {string|null} - 返回找到的cookie值或null
     */
    get(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
        }
        return null;
    },

    /**
     * 存储整数*/
    setInt(key, value) {
        // 存储整数数据
        localStorage.setItem(key, value);
    },

    /**
     * 获取整数*/
    getInt(key) {
        // 获取整数数据，做一下必要的转化
        if (localStorage.getItem(key) === null) {
            return 0;
        }
        return localStorage.getItem(key);
    },

    /**
     * 存储json数据*/
    setJson(key, value) {
        // 存储json数据
        localStorage.setItem(key, JSON.stringify(value));
    },

    /**
     * 获取json数据*/
    getJson(key) {
        // 获取json数据
        return JSON.parse(localStorage.getItem(key));
    },
    /**
     * 清除所有cookie和localStorage的内容*/
    /**
     * 清除所有cookie
     */
    clearAll() {
        // 清除localStorage的内容
        localStorage.clear();

        // 获取所有cookie
        const cookies = document.cookie.split(";");

        // 遍历所有cookie并删除
        for (let c of cookies) {
            const eqPos = c.indexOf("=");
            const name = eqPos > -1 ? c.substr(0, eqPos) : c;
            document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
        }
    },

    showAllCookie(){
        const cookies = document.cookie.split(";");

        // 遍历所有cookie
        for (let c of cookies) {
            console.log(c);
        }
    }

};
