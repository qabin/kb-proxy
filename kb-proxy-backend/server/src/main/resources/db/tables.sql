CREATE TABLE `env_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL COMMENT '环境名称',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `hosts` longtext COMMENT 'hosts配置',
  `status` int(11) NOT NULL DEFAULT '-1' COMMENT '-1 未激活  1激活',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `mock_proxy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL COMMENT 'mock名称',
  `description` varchar(1024) DEFAULT NULL COMMENT 'mock描述',
  `url` longtext NOT NULL COMMENT '请求Url',
  `method` varchar(256) NOT NULL COMMENT '请求方式',
  `code` int(11) NOT NULL DEFAULT '200' COMMENT '状态码',
  `response` longtext COMMENT '响应结果',
  `headers` longtext COMMENT '响应头',
  `is_used` int(11) NOT NULL DEFAULT '1' COMMENT '是否启用，1 启用 0禁用',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `only_uri` int(11) NOT NULL DEFAULT '0' COMMENT '只匹配 请求路径。  1 是 0 否',
  `domain` varchar(1024) NOT NULL COMMENT '域名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `mock_proxy_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mock_id` int(11) NOT NULL COMMENT 'mock_proxy表id',
  `url` longtext NOT NULL COMMENT '请求url',
  `method` varchar(256) NOT NULL COMMENT '请求方式',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `response` longtext COMMENT '响应结果',
  `headers` longtext COMMENT '响应headers',
  `code` int(11) DEFAULT '200' COMMENT '状态码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `proxy_request_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `method` varchar(256) DEFAULT NULL COMMENT '请求方式',
  `protocol` varchar(256) DEFAULT NULL COMMENT '请求协议 ',
  `code` varchar(256) DEFAULT NULL COMMENT '状态码',
  `host` varchar(1024) DEFAULT NULL COMMENT '域名',
  `path` longtext COMMENT '请求uri',
  `mime_type` varchar(1024) DEFAULT NULL COMMENT '请求mime信息',
  `header` longtext COMMENT '请求头',
  `body` longtext COMMENT '请求体',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `url` longtext COMMENT '请求url',
  `proxy_port` int(11) DEFAULT NULL COMMENT '代理端口号',
  `mock` int(11) DEFAULT '0' COMMENT '0 未命中Mock  1命中Mock',
  `ip` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `proxy_response_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `request_detail_id` int(11) NOT NULL COMMENT 'proxy_request_detail表id',
  `code` varchar(45) DEFAULT NULL COMMENT '状态码',
  `header` longtext COMMENT '响应头',
  `body` longtext COMMENT '响应体',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `proxy_port` int(11) DEFAULT NULL COMMENT '端口号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `repeater_request_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL COMMENT '重发请求名称',
  `description` varchar(1024) DEFAULT NULL COMMENT '描述',
  `url` varchar(1024) DEFAULT NULL COMMENT '请求url',
  `method` varchar(45) NOT NULL COMMENT '请求方式',
  `headers` longtext COMMENT '请求头',
  `request_json` longtext COMMENT '请求json数据',
  `request_form` longtext COMMENT '请求form数据',
  `body_type` varchar(45) DEFAULT NULL COMMENT '请求体类型',
  `folder_id` int(11) NOT NULL COMMENT '文件夹id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `repeater_request_folder` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL COMMENT '文件夹名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `repeater_request_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(1024) NOT NULL COMMENT '请求url',
  `method` varchar(45) NOT NULL COMMENT '请求方式',
  `headers` longtext COMMENT '请求头',
  `request_json` longtext COMMENT '请求json数据',
  `request_form` longtext COMMENT '请求form数据',
  `body_type` varchar(45) DEFAULT NULL COMMENT '请求体类型',
  `response_headers` longtext COMMENT '响应头',
  `response_body` longtext COMMENT '响应体',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login_name` varchar(256) NOT NULL COMMENT '登录账号',
  `login_pwd` varchar(256) NOT NULL COMMENT '登录密码',
  `nick_name` varchar(256) NOT NULL COMMENT '昵称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '-1 代理关闭  1代理开启',
  PRIMARY KEY (`id`),
  UNIQUE KEY `login_name_UNIQUE` (`login_name`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8;
