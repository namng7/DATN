CREATE TABLE user
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL,
    password     VARCHAR(255) NOT NULL,
    email        VARCHAR(100) NOT NULL,
    fullname     VARCHAR(100) NOT NULL,
    `status`     INT       DEFAULT 0,
    `role`       INT       DEFAULT 0,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Thời gian cập nhật, tự động thay đổi mỗi khi có cập nhật
    UNIQUE (username)
);

CREATE TABLE company
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    code           VARCHAR(50),
    name           VARCHAR(255) NOT NULL,
    description    VARCHAR(4000),
    industry       VARCHAR(100),
    tax            VARCHAR(100) NOT NULL,
    email          VARCHAR(100) NOT NULL,
    phone          VARCHAR(20)  NOT NULL,
    represent      VARCHAR(100) NOT NULL,
    address        VARCHAR(500),
    bank           VARCHAR(100) NOT NULL,
    `status`       INT       DEFAULT 0,
    create_by      INT,
    created_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by     INT,
    updated_reason VARCHAR(1000),
    updated_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Th?i gian c?p nh?t, t? ??ng thay ??i m?i khi c� c?p nh?t
    bussiness_care INT,
    user_id        INT          NOT NULL,
    UNIQUE (user_id)
);

CREATE TABLE wallet
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    company_id    int,
    balance       int default 0,
    last_topup_id int default -1,
    status        int DEFAULT 0,
    FOREIGN KEY (company_id) REFERENCES company (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE `transaction_top_up` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `wallet_id` int(11) NOT NULL,
  `value` int(11) NOT NULL,
  `wallet_balance` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT 0,
  `bussiness_id` int(11) NOT NULL,
  `transaction_time` timestamp NOT NULL DEFAULT current_timestamp(),
  `accept_time` timestamp ,
  PRIMARY KEY (`id`),
  KEY `idx_name` (`bussiness_id`),
  KEY `trans_top_up_trans_time_idx` (`transaction_time`),
  KEY `trans_top_up_walletID_idx` (`wallet_id`),
  CONSTRAINT `transaction_top_up_ibfk_1` FOREIGN KEY (`wallet_id`) REFERENCES `wallet` (`id`),
  CONSTRAINT `transaction_top_up_ibfk_2` FOREIGN KEY (`bussiness_id`) REFERENCES `user` (`id`)
);


create table webservice_config
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    ws_name      varchar(50),
    api_url      varchar(500),
    msg_template varchar(1000),
    status       int DEFAULT 0,
    retry_num    int default 0
);
create table package_config
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    package_code    varchar(50)   not null,
    package_name    varchar(50)   not null,
    description     varchar(1000) not null,
    status          int       DEFAULT 0,
    add_value       int       default 0,
    ws_id           int           not null,
    valid_time      TIMESTAMP,
    create_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_user_id  int           not null,
    price           int       default 0,
    updated_user_id int,
    updated_reason  varchar(100),
    foreign key (ws_id) references webservice_config (id),
    foreign key (create_user_id) references user (id),
    foreign key (updated_user_id) references user (id),
    UNIQUE (package_code)
);

create table gamecode_model
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    model_name      varchar(50) not null,
    model_code      varchar(50) not null,
    discount        int       default 0,
    description     varchar(1000),
    status          int       DEFAULT 0,
    start_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valid_date      TIMESTAMP,
    create_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_user_id  int         not null,
    updated_date    TIMESTAMP,
    updated_user_id int,
    package_id      int         not null,
    number_required int         not null,
    foreign key (create_user_id) references user (id),
    foreign key (updated_user_id) references user (id),
    foreign key (package_id) references package_config (id)
);

CREATE TABLE `transaction_buy_gamecode` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) DEFAULT NULL,
  `wallet_before` int(11) NOT NULL,
  `wallet_after` int(11) NOT NULL,
  `wallet_consumption` int(11) NOT NULL,
  `transaction_time` timestamp NOT NULL DEFAULT current_timestamp(),
  `total_item` int(11) NOT NULL,
  `model_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `model_id` (`model_id`),
  KEY `trans_buy_code_companyID` (`company_id`),
  KEY `trans_buy_code_trans_time_idx` (`transaction_time`),
  CONSTRAINT `transaction_buy_gamecode_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`),
  CONSTRAINT `transaction_buy_gamecode_ibfk_2` FOREIGN KEY (`model_id`) REFERENCES `gamecode_model` (`id`)
);

create table service_config
(
    id                INT AUTO_INCREMENT PRIMARY KEY,
    company_id        int,
    gamecode_model_id int,
    create_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    start_date        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valid_date        TIMESTAMP,
    export_status     int       default 0,
    model_id          int,
    FOREIGN KEY (company_id) REFERENCES company (id),
    FOREIGN KEY (model_id) REFERENCES gamecode_model (id),
    FOREIGN KEY (gamecode_model_id) REFERENCES gamecode_model (id)
);

create table configuration
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    `key`     varchar(100),
    content   varchar(1000),
    module_id int DEFAULT 0
);

create table gamecode_detail
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    gamecode    varchar(12),
    serial      VARCHAR(12),
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    start_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valid_date  TIMESTAMP,
    status      int       default 0
);

create table transaction_buy_gamecode_detail(
	id              INT AUTO_INCREMENT PRIMARY KEY,
	trans_id int not null,
	serial varchar(100) not null,
	create_date timestamp not null,
	start_date timestamp not null,
	valid_date timestamp ,
	status int,
	index transId_idx (trans_id),
	foreign key (trans_id) references transaction_buy_gamecode (id)
);