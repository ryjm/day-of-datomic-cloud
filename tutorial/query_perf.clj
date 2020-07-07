;   Copyright (c) Cognitect, Inc. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(require '[datomic.client.api :as d])
(def client (d/client {:server-type :dev-local
                       :system "datomic-samples"}))
(def conn (d/connect client {:db-name "mbrainz-subset"}))
(def db (d/db conn))
(set! *print-length* 100)

(def mccartney [:artist/gid #uuid "ba550d0e-adac-4864-b88b-407cab5e76af"])

;; This query leads with a where clause that must consider *all* releases
;; in the database.  SLOW.
(dotimes [_ 5]
  (time
   (d/q '[:find ?name
          :in $ ?artist
          :where [?release :release/name ?name]
                 [?release :release/artists ?artist]]
        db
        mccartney)))

;; The same query, but reordered with a more selective where clause first.
;; significantly faster.
(dotimes [_ 5]
  (time
   (d/q '[:find ?name
          :in $ ?artist
          :where [?release :release/artists ?artist]
                 [?release :release/name ?name]]
        db
        mccartney)))