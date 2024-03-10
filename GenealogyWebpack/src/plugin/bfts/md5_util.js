import SparkMD5 from './spark-md5.min.js'


export function getFileBlockMd5(file) {    //这里假设直接将文件选择框的dom引用传入
    return new Promise(function(resolve,reject){
        var fileReader = new FileReader();    //创建FileReader实例
        fileReader.onload = async function (e) {    //FileReader的load事件，当文件读取完毕时触发
            // e.target指向上面的fileReader实例
            if (file.size != e.target.result.length) {    //如果两者不一致说明读取出错
                reject(e)
            } else {
                var result =SparkMD5.hashBinary(e.target.result).toString();
                resolve(result);
            }
        };
        fileReader.onerror = function (e) {    //如果读取文件出错，取消读取状态并弹框报错
            reject(e)
        };
        fileReader.readAsBinaryString(file);    //通过fileReader读取文件二进制码
    })

}

export async function getFileHashMd5(file) {
    return new Promise((resolve,reject) => {
      const spark = new SparkMD5.ArrayBuffer();
      const reader = new FileReader();
      // 文件大小
      const size = file.size;
      let offset = 2 * 1024 * 1024;

      let chunks = [file.slice(0, offset)];

      // 前面100K

      let cur = offset;
      while (cur < size) {
        // 最后一块全部加进来
        if (cur + offset >= size) {
          chunks.push(file.slice(cur, cur + offset));
        } else {
          // 中间的 前中后去两个字节
          const mid = cur + offset / 2;
          const end = cur + offset;
          chunks.push(file.slice(cur, cur + 2));
          chunks.push(file.slice(mid, mid + 2));
          chunks.push(file.slice(end - 2, end));
        }
        // 前取两个字节
        cur += offset;
      }
      // 拼接
      reader.readAsArrayBuffer(new Blob(chunks));
      reader.onload = e => {
        spark.append(e.target.result);
        resolve(spark.end());
      };

        reader.onerror = e =>{
            reject(e)
        };
    });
  }