(ns h3m-lwp-clj.assets
  (:import [com.badlogic.gdx.graphics Texture]
           [com.badlogic.gdx.graphics.g2d TextureRegion TextureAtlas TextureAtlas$AtlasRegion]
           [com.badlogic.gdx.assets AssetManager]
           [com.badlogic.gdx.assets.loaders TextureAtlasLoader$TextureAtlasParameter]
           [com.badlogic.gdx.utils Array]))


(def ^String objects-atlas "sprites/all.atlas")


(defonce objects-info (atom {}))


(def ^AssetManager manager
  (doto (new AssetManager)
    (.load objects-atlas TextureAtlas (new TextureAtlasLoader$TextureAtlasParameter true))))


(defn init []
  (reset!
   objects-info
   (try
     ; TODO AssetLoader?
     (read-string (slurp "sprites/all.edn"))
     (catch Exception _
       (println "Failed to read all.edn")
       {})))
  (.finishLoading manager)
  (Texture/setAssetManager manager))


(defn get-sprite-info [def-name]
  (get @objects-info def-name))


(defn get-object-frame [def-name offset]
  ^TextureRegion
  (let [^TextureAtlas atlas (.get manager objects-atlas)
        ^TextureAtlas$AtlasRegion frame (.findRegion atlas def-name offset)]
    (when (nil? frame)
      (throw (new Exception (format "def %s with offset %d not found in atlas" def-name offset))))
    frame))


(defn get-terrain-sprite [def-name index]
  ^Array
  (let [^TextureAtlas atlas (.get manager objects-atlas)
        sprite-info (get-sprite-info def-name)
        {frames-order :order} sprite-info
        offset (nth frames-order index)
        region-name (format "%s/%d" def-name offset)
        ^Array frames (.findRegions atlas region-name)]
    (when (.isEmpty frames)
      (throw (new Exception (format "def %s with index %d not found in atlas" def-name index))))
    frames))