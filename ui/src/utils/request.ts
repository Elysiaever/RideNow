import axios, {type AxiosInstance} from "axios";

const request: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 5000,
    headers: {
        "Content-Type": "application/json",
    },
});

//请求拦截器
request.interceptors.request.use( )

//响应拦截器
request.interceptors.response.use( )

export default request;