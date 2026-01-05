// 百度地图API加载工具
interface BaiduMapLoaderOptions {
  ak: string;
  version?: string;
  callbackName?: string;
}

class BaiduMapLoader {
  private static instance: BaiduMapLoader;
  private isLoading = false;
  private loaded = false;
  private callbacks: Array<() => void> = [];

  public static getInstance(): BaiduMapLoader {
    if (!BaiduMapLoader.instance) {
      BaiduMapLoader.instance = new BaiduMapLoader();
    }
    return BaiduMapLoader.instance;
  }

  async load(options: BaiduMapLoaderOptions): Promise<void> {
    const { ak, version = '3.0', callbackName = 'BMapCallback' } = options;

    return new Promise((resolve, reject) => {
      // 如果已经加载完成，直接返回
      if (this.loaded && (window as any).BMap) {
        resolve();
        return;
      }

      // 如果正在加载，将回调加入队列
      if (this.isLoading) {
        this.callbacks.push(() => {
          if ((window as any).BMap) {
            resolve();
          } else {
            reject(new Error('Baidu Map API failed to load'));
          }
        });
        return;
      }

      this.isLoading = true;

      // 创建全局回调函数
      (window as any)[callbackName] = () => {
        this.loaded = true;
        this.isLoading = false;
        
        // 解决当前Promise
        resolve();
        
        // 执行等待队列中的回调
        this.callbacks.forEach(callback => callback());
        this.callbacks = [];
      };

      // 创建script标签加载百度地图API
      const script = document.createElement('script');
      script.type = 'text/javascript';
      script.src = `https://api.map.baidu.com/api?v=${version}&ak=${ak}&callback=${callbackName}`;
      
      // 错误处理
      script.onerror = () => {
        this.isLoading = false;
        reject(new Error('Failed to load Baidu Map API'));
      };

      // 添加到head中
      document.head.appendChild(script);
    });
  }
}

export const loadBaiduMap = (ak: string) => {
  const loader = BaiduMapLoader.getInstance();
  return loader.load({ ak });
};