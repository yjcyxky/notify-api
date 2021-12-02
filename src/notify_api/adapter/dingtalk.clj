(ns notify-api.adapter.dingtalk
  "Send message to dingtalk.
   
   Please access https://developers.dingtalk.com/document/app/message-type for more more details.
   
   # Setup
   (def access-token \"1fe1840503d9cb76277a48d79e9aa629ee6519e6437cd4aeeef194659ef4703a\")
   (def secret \"SEC0068c84057217b3b8a32eeaa31d64b5c492116f431a7b641fa5558380661c81b\")
   (setup-dingtalk access-token secret)
   
   # Send text message
   (send-text-msg! \"This is a test.\")
   
   # Send link message
   (send-link-msg! \"EXAMPLE\" \"This is a description for the link message\" 
                   \"https://dbd0040.blob.core.windows.net/images/Example_MyTicket.jpg\" 
                   \"https://myticket.co.uk/artists/example\")
   
   # Send markdown message
   (send-markdown-msg! \"首屏会话透出的展示内容\" \"# 这是支持markdown的文本 \n## 标题2  \n* 列表1 \n![alt 啊](https://img.alicdn.com/tps/TB1XLjqNVXXXXc4XVXXXXXXXXXX-170-64.png)\")
   
   # Send action card message
   (send-action-card! \"是透出到会话列表和通知的文案\" \"支持markdown格式的正文内容\" \"查看详情\" \"https://open.dingtalk.com\")"
  (:require [lambdaisland.uri :as uri-lib]
            [clj-http.client :as client]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json])
  (:import (javax.crypto Mac)
           (javax.crypto.spec SecretKeySpec)
           (org.apache.commons.codec.binary Base64)
           (java.net URLEncoder)))

(def ^:private access-token (atom nil))
(def ^:private secret (atom nil))

(defn- timestamp
  []
  (System/currentTimeMillis))

(defn- string-to-sign
  [timestamp]
  (str timestamp "\n" @secret))

(defn- secretKeyInst [secret mac]
  (SecretKeySpec. (.getBytes secret "UTF-8") (.getAlgorithm mac)))

(defn- sign
  "Returns the signature of a string with a given
   secret, using a SHA-256 HMAC."
  [secret string]
  (let [mac       (Mac/getInstance "HmacSHA256")
        secretKey (secretKeyInst secret mac)]
    (-> (doto mac
          (.init secretKey)
          (.update (.getBytes string "UTF-8")))
        .doFinal)))

(defn- encode
  [sign-data]
  (-> sign-data
      (Base64/encodeBase64)
      (String.)
      (URLEncoder/encode "UTF-8")))

(defn- gen-signed-string
  [timestamp]
  (->> (string-to-sign timestamp)
       (sign @secret)
       (encode)))

(defn- make-webhook-url
  ([] (let [timestamp (timestamp)]
        (make-webhook-url @access-token timestamp (gen-signed-string timestamp))))
  ([query]
   (str (assoc (uri-lib/uri "https://oapi.dingtalk.com/robot/send")
               :query query)))
  ([access-token timestamp sign]
   (make-webhook-url (format "access_token=%s&timestamp=%s&sign=%s" access-token timestamp sign))))

(defn- assert-config
  "Something is wrong."
  []
  (assert (and @access-token @secret)
          "You need to setup dingtalk before sending message."))

(defn- send-msg!
  [msg]
  (assert-config)
  (let [ret-msg (client/post (make-webhook-url)
                             {:body               msg
                              :content-type       :json
                              :socket-timeout     1000      ;; in milliseconds
                              :connection-timeout 1000      ;; in milliseconds
                              :accept             :json})]
    (log/info "Send message to dingtalk: " (:body ret-msg))
    ret-msg))

(defn- markdown-msg
  [^String title ^String content]
  (json/write-str
   {:msgtype  "markdown"
    :markdown {:title title
               :text  content}}))

(defn- text-msg
  [^String content]
  (json/write-str
   {:msgtype "text"
    :text    {:content content}}))

(defn- link-msg
  [^String title ^String content ^String pic-url ^String msg-url]
  (json/write-str {:msgtype "link"
                   :link    {:text       content
                             :title      title
                             :picUrl     pic-url
                             :messageUrl msg-url}}))

(defn- action-card
  [^String title ^String content ^String single-title ^String single-url]
  (json/write-str {:msgtype    "actionCard"
                   :actionCard {:title          title
                                :text           content
                                :hideAvatar     "0"
                                :btnOrientation "0"
                                :singleTitle    single-title
                                :singleURL      single-url}}))

(defn setup-dingtalk
  [^String dingtalk-token ^String dingtalk-secret]
  (reset! access-token dingtalk-token)
  (reset! secret dingtalk-secret))

(defn send-text-msg!
  "Send text message to dingtalk."
  [content]
  (send-msg! (text-msg content)))

(defn send-link-msg!
  "Send link message to dingtalk."
  [title content pic-url msg-url]
  (send-msg! (link-msg title content pic-url msg-url)))

(defn send-markdown-msg!
  "Send markdown message to dingtalk.
   
   Access [dingtalk api doc](https://developers.dingtalk.com/document/app/message-type/title-0ut-020-hz2) for more details."
  [title content]
  (send-msg! (markdown-msg title content)))

(defn send-action-card!
  "Send action card to dingtalk.
  
   Access [dingtalk api doc](https://developers.dingtalk.com/document/app/message-type/title-0ej-p5e-kjg) for more details."
  [title content single-title single-url]
  (send-msg! (action-card title content single-title single-url)))
