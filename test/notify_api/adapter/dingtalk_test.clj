(ns notify-api.adapter.dingtalk-test
  (:require [clojure.test :refer :all]
            [notify-api.adapter.dingtalk :as dingtalk]))

(defn assert-get
  "get a value from the environment, otherwise throw an exception detailing the problem"
  [key-name]
  (or (System/getenv key-name)
      (throw (Exception. (format "please define %s in the test environment" key-name)))))

(use-fixtures
  :once
  (fn [f]
    (let [access-token (assert-get "DINGTALK_ACCESS_TOKEN")
          secret (assert-get "DINGTALK_SECRET")]
      (dingtalk/setup-dingtalk access-token secret)
      (f))))

(deftest test-apps
  (testing "Test dingtalk module."
    (is (= "{\"errcode\":0,\"errmsg\":\"ok\"}"
           (:body (dingtalk/send-text-msg! "test"))))

    (is (= "{\"errcode\":0,\"errmsg\":\"ok\"}"
           (:body (dingtalk/send-link-msg! "EXAMPLE" "This is a description for the link message"
                                           "https://dbd0040.blob.core.windows.net/images/Example_MyTicket.jpg"
                                           "https://myticket.co.uk/artists/example"))))

    (is (= "{\"errcode\":0,\"errmsg\":\"ok\"}"
           (:body (dingtalk/send-markdown-msg! "首屏会话透出的展示内容" "# 这是支持markdown的文本 \n## 标题2  \n* 列表1 \n![alt 啊](https://img.alicdn.com/tps/TB1XLjqNVXXXXc4XVXXXXXXXXXX-170-64.png)"))))

    (is (= "{\"errcode\":0,\"errmsg\":\"ok\"}"
           (:body (dingtalk/send-action-card! "是透出到会话列表和通知的文案" "支持markdown格式的正文内容" "查看详情" "https://open.dingtalk.com"))))))
