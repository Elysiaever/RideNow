CREATE TABLE t_user (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    phone           VARCHAR(20) UNIQUE,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

COMMENT ON TABLE t_user IS '乘客用户表';

COMMENT ON COLUMN t_user.id            IS '主键 ID';
COMMENT ON COLUMN t_user.username      IS '用户名';
COMMENT ON COLUMN t_user.password_hash IS '密码（加密后）';
COMMENT ON COLUMN t_user.phone         IS '手机号';
COMMENT ON COLUMN t_user.created_at    IS '创建时间';
COMMENT ON COLUMN t_user.updated_at    IS '更新时间';



CREATE TABLE t_ride (
                        id                BIGSERIAL PRIMARY KEY,
                        passenger_id      BIGINT NOT NULL,
                        driver_id         BIGINT,
                        start_lng         DOUBLE PRECISION NOT NULL,
                        start_lat         DOUBLE PRECISION NOT NULL,
                        end_lng           DOUBLE PRECISION,
                        end_lat           DOUBLE PRECISION,
                        status            VARCHAR(30) NOT NULL,  -- CREATED / MATCHING / ASSIGNED / STARTED / COMPLETED / CANCELLED
                        created_at        TIMESTAMP DEFAULT NOW(),
                        updated_at        TIMESTAMP DEFAULT NOW()
);

COMMENT ON TABLE t_ride IS '行程表：记录一次用户叫车的全过程';

COMMENT ON COLUMN t_ride.id             IS '行程ID';
COMMENT ON COLUMN t_ride.passenger_id   IS '乘客ID';
COMMENT ON COLUMN t_ride.driver_id      IS '司机ID（匹配后填）';
COMMENT ON COLUMN t_ride.start_lng      IS '起点经度';
COMMENT ON COLUMN t_ride.start_lat      IS '起点纬度';
COMMENT ON COLUMN t_ride.end_lng        IS '终点经度';
COMMENT ON COLUMN t_ride.end_lat        IS '终点纬度';
COMMENT ON COLUMN t_ride.status         IS '行程状态：CREATED / MATCHING / ASSIGNED / STARTED / COMPLETED';
COMMENT ON COLUMN t_ride.created_at     IS '创建时间';
COMMENT ON COLUMN t_ride.updated_at     IS '更新时间';


CREATE TABLE t_driver (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    status          VARCHAR(20) DEFAULT 'OFFLINE',   -- AVAILABLE / BUSY / OFFLINE
    rating          NUMERIC(3,2) DEFAULT 5.0,        -- 司机评分
    vehicle_brand   VARCHAR(50),                     -- 车辆品牌（可选）
    vehicle_model   VARCHAR(50),                     -- 车型（可选）
    vehicle_number  VARCHAR(20),                     -- 车牌号（可选）
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW(),
    
    CONSTRAINT fk_driver_user FOREIGN KEY (user_id)
        REFERENCES t_user(id) ON DELETE CASCADE
);

COMMENT ON TABLE t_driver IS '司机表：包含司机账号、状态、评分、车辆等信息';

COMMENT ON COLUMN t_driver.id             IS '主键 ID';
COMMENT ON COLUMN t_driver.user_id        IS '关联的用户 ID（外键）';
COMMENT ON COLUMN t_driver.status         IS '司机状态：AVAILABLE / BUSY / OFFLINE';
COMMENT ON COLUMN t_driver.rating         IS '司机评分';
COMMENT ON COLUMN t_driver.vehicle_brand  IS '车辆品牌';
COMMENT ON COLUMN t_driver.vehicle_model  IS '车辆型号';
COMMENT ON COLUMN t_driver.vehicle_number IS '车牌号';
COMMENT ON COLUMN t_driver.created_at     IS '创建时间';
COMMENT ON COLUMN t_driver.updated_at     IS '更新时间';
