## 在线编译网站

前端网页基于 `Vue`编写, 后端使用`Spring MVC` , 代码编译服务原理是调用预先设置好的docker镜像, 做到安全隔离和运行资源控制.

### 后端细节

#### 接口

[![Run in Postman](https://image-storage-1258004334.cos.ap-chengdu.myqcloud.com/button.svg)](https://app.getpostman.com/run-collection/f37cc99b1f7d23a4254a)

#### 配置信息

```properties
# 运行超时提示
runTimeOutMsg = Program run timeout
# 运行超时时间 单位ms
timeOut = 30000
# 超过最大同时运行限制提示
waitTimeOutMsg = Don't have the resources to run
# 最大运行限制数量
concurrentRunNumber = 4
# 运行 kotlin代码的docker镜像名,和对应的docker内代码文件地址
kotlinImageName = "fanickee/kotlin"
kotlinRunPath = "/opt/main.kt"
# 运行 python代码的docker镜像名,和对应的docker内代码文件地址
python3ImageName = "fanickee/python3"
python3RunPath = "/usr/src/app/main.py"
# 运行时占用的最大运行内存
memoryLimit = 300
# 运行时使用的CPU 核心
cpuLimit = 0.5
# docker运行容器的命令模板
dockerRun = docker run --rm --name {containerName} -m %sM --cpus %s -v {filepath}:{runPath} {imageName}
dockerDel = docker rm -f {containerName}
# 前端获取运行代码后,暂存路径
fileCatchPath = C:/upload
```

**注意:** `fileCatchPath` 路径必须存在

#### docker 镜像要求

控制台打印的结果必须在第一行加上 `SUCCESS_FLAG`或者`ERROR_FLAG`,用来标识运行结果是否有错误

模板见`docker build`文件夹

### 前端细节

- 代码高亮
- 结果展示
- 错误提示
- 界面JetBrain风格,过渡流畅

#### 代码高亮组件使用说明:  [CodeMirror](http://codemirror.net/)

#### 模板化

1. 在`App.vue` 里引用`CodeMirror`需要的资源

2. 在`main.js` 里修改axios的全局的请求基础地址

3. 修改 `compileMsg`对象

   ```js
   let compileMsg = {
     // 对应CodeMirror的高亮风格
     mode: "text/x-python",
     // 对应后端接口名
     local: "PYTHON3",
     // 第一次打开网页时,代码编辑器显示的代码
     defaultValue: "print(\"hello world !!!\")",
     // 使用正则分析后端传来的编译错误结果,给对应代码打上标记
     // result:Arrays<String>: 错误编译结果字符串
     // instance.setGutterMarker,instance.setTextMarker分别对应设置边沟错误提示和设置文本断错误提示
     analyzeError(result,instance) {
       let a = /File "\.\/main\.py", line (\d+)(, in <module>)?\n.+?(\w+?Error:.+)/gs
       for (const j of result) {
         let res = j.matchAll(a)
         let c = true
         while (c) {
           let b = res.next()
           if (b.value && b.value[0]) {
             instance.setGutterMarker(parseInt(b.value[1]) - 1, b.value[3])
             instance.setTextMarker({line: parseInt(b.value[1]) - 1, ch: 0}, {line: parseInt(b.value[1]), ch: 0})
           } else {
             c = false
           }
         }
       }
     }
   }
   ```

4. 修改`Tools.vue`组件的svg图标(`div.btnContain > div:nth-child(3)>svg`)

   ![image-20210103153742154](https://image-storage-1258004334.cos.ap-chengdu.myqcloud.com/image-20210103153742154.png)
   
5. 修改vue.config.js里的`publicPath` 对应servlet的静态资源路径

#### 网页展示

![image-20210103153524821](https://image-storage-1258004334.cos.ap-chengdu.myqcloud.com/image-20210103153524821.png)

![image-20210103153631357](https://image-storage-1258004334.cos.ap-chengdu.myqcloud.com/image-20210103153631357.png)
