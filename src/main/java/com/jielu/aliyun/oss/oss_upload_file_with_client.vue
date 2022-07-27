<template>
  <div class="oss">
    <img :src="oss_imgurl" alt="" />
    <input
      type="file"
      name="companyLogo"
      id="file0"
      class="file_companyLogo"
      multiple="multiple"
      @change="companyLogo($event)"
    />
    <el-button type="primary" @click="uploading(true)">保存</el-button>
  </div>
</template>
<script>

 var cachedMap=new Map();
function str2UTF8(str){
	var bytes = new Array();
	var len,c;
	len = str.length;
	for(var i = 0; i < len; i++){
		c = str.charCodeAt(i);
		if(c >= 0x010000 && c <= 0x10FFFF){
			bytes.push(((c >> 18) & 0x07) | 0xF0);
			bytes.push(((c >> 12) & 0x3F) | 0x80);
			bytes.push(((c >> 6) & 0x3F) | 0x80);
			bytes.push((c & 0x3F) | 0x80);
		}else if(c >= 0x000800 && c <= 0x00FFFF){
			bytes.push(((c >> 12) & 0x0F) | 0xE0);
			bytes.push(((c >> 6) & 0x3F) | 0x80);
			bytes.push((c & 0x3F) | 0x80);
		}else if(c >= 0x000080 && c <= 0x0007FF){
			bytes.push(((c >> 6) & 0x1F) | 0xC0);
			bytes.push((c & 0x3F) | 0x80);
		}else{
			bytes.push(c & 0xFF);
		}
	}
	return bytes;
}
export default {
  data() {
    return {
      imgFile: "",
      oss_imgurl: "",
    };
  },
  created() {
    console.log(window.OSS);
  },
  mounted() {
      const oScript = document.createElement("script")
      oScript.type = "text/javascript"
      oScript.src = "https://gosspublic.alicdn.com/aliyun-oss-sdk-4.4.4.min.js"
      document.body.appendChild(oScript);
    },

  methods: {
     companyLogo(event) {
      var file = event.target.files[0];
      var fileName=file.name;
      var fileSize = file.size; //file size
      var filetType = file.type; //file type
      //create file object
      if (fileSize <= 10240 * 1024*5) {
        if (checkType(fileName)) {
          this.imgFile = file;
          this.uploading(true)
        } else {
          this.$message.error("图片格式不正确");
        }
      } else {
        this.$message.error("图片大小不正确");
      }

    },

     checkType(name){
     return /\.(gif|jpg|jpeg|png|mp4)$/i.test(name)
     },
    //Convert the file into blob
    readFileAsBuffer(file) {
      const reader = new FileReader();
      return new Promise((resolve) => {
        reader.readAsDataURL(file);
        reader.onload = function () {
          const base64File = reader.result.replace(
            /^data:\w+\/\w+;base64,/,
            ""
          );
          resolve(new window.OSS.Buffer(base64File, "base64"));
        };
      });
    },
      //checkPoint upload to oss,this is slice the file inputStream
     async  multipart(file) {
                 var res;
                 let file_res=readFileAsBuffer(file);//base64 encode
                 if(res=cachedMap.get(file_rs)!=null){
                   return res;
                 }
                    try {
                        let result = await client.multipartUpload('upload-file', file,
                            { //this method "progress" from oss-client.js
                                async progress(p, _checkpoint) {
                                },
                                meta: { year: 2022, people: "test" },
                                mime: this.file.type,
                            });
                         m.set(file_re, result); //storage the result to map


                    } catch (e) {
                        console.log(e);
                    }


    hashCode(base64EncodeStr) {
        let value=str2UTF8(base64EncodeStr)
        let h = 0;
        for (var v : value) {
            h = 31 * h + (v & 0xff);
        }
        return h;
    },

    async uploading(flag) {
      // console.log(document.getElementById("file0").value);
      if (flag) {
        var file_re = await this.readFileAsBuffer(this.imgFile);
        //set the hashed value to cachedMap 
        let hashedEncode64Str=hashCode(file_rs);
        if(res=cachedMap.get(hashedEncode64Str)!=null){
          return res;
        }
        console.log(this.imgFile);
        this.$api.ossststoken().then((res) => {
          let myData = res.data.data.date;
          let client = new window.OSS.Wrapper({
            region: "oss-cn-shanghai", //oss address
            accessKeyId: myData.Credentials.AccessKeyId, //ak
            accessKeySecret: myData.Credentials.AccessKeySecret, //secret
            stsToken: myData.Credentials.SecurityToken,
            bucket: "jielu", //ossname
          });
          var imgtype = this.imgFile.type.substr(6, 4);
          var store = `${new Date().getTime()}.${imgtype}`;
          client.put(store, file_re).then((res) => {
            //res is oss url
            m.set(hashedEncode64Str, res); //set the  oss url result get from oss to map
            var a = client.signatureUrl(store);
            this.oss_imgurl = a;
          });
        });
      }
    },
  },
};
</script>
