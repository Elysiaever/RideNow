import axios from 'axios'

const api = axios.create({
    baseURL: 'http://localhost:80',  // 网关地址（端口80）
    timeout: 5000
})
export default api
