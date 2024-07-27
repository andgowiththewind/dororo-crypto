<h1 align="center" style="color:#8a1874">web-user-interface-design</h1>

# 说明

`Vue2`项目；



# 创建项目

```shell
vue create web-user-interface-design
```



# 安装`ElementUI`

```shell
npm i element-ui -S
```

> `main.js`修改内容

```javascript
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

Vue.use(ElementUI);
```





# 安装`font-awesome`

```javascript
npm install font-awesome --save
```

> `main.js`修改内容

```javascript
import 'font-awesome/css/font-awesome.css';
```



# 安装`moment.js`

```javascript
npm install moment --save
```

> `main.js`修改内容

```javascript
import moment from "moment";

new Vue({
    beforeCreate() {
        moment.locale('zh-cn');//全局使用moment.js,使用时示例:`this.$moment(new Date()).format('YYYY-MM-DD HH:mm:ss')`
        Vue.prototype.$moment = moment;
    },
    render: h => h(App),
}).$mount('#app')
```





# 安装`axios`

```javascript
npm i axios --save
```





# 安装`nanoId`

```javascript
npm install nanoid --save
```





# 安装`nanoid`

```shell
npm install nanoid --save
```

> `.vue`文件引入

```vue
import {nanoid} from 'nanoid';
```

> 直接使用即可

```javascript
let nanoId = nanoid();
devConsoleLog('nanoId', nanoId);

let anotherNanoId = nanoid(22);
devConsoleLog('anotherNanoId', anotherNanoId);
```



# 安装`crypto-js`

```
npm install crypto-js --save
```

