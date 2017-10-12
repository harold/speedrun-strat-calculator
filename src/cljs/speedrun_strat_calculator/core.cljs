(ns speedrun-strat-calculator.core
  (:require [reagent.core :as reagent]))

(enable-console-print!)

(defonce app-state*
  (reagent/atom {:tricks []}))

(defn trick
  [i {:keys [trick-name trick-time success-chance id] :as local-trick}]
  (fn [i {:keys [trick-name trick-time success-chance id] :as local-trick}]
    [:div.trick
     [:input (merge {:placeholder "Trick name..."
                     :on-change (fn [e]
                                  (let [new-name (-> e .-target .-value)]
                                    (if-not (empty? new-name)
                                      (swap! app-state* update :tricks
                                             assoc-in [i :trick-name] new-name)
                                      (swap! app-state* update :tricks
                                             assoc i (dissoc local-trick :trick-name)))))}
                    (when trick-name
                      {:value trick-name}))]
     [:input (merge {:placeholder "Time trick encountered..."
                     :on-change (fn [e]
                                  (let [new-time-str (-> e .-target .-value)]
                                    (if-not (empty? new-time-str)
                                      (swap! app-state* update :tricks
                                             assoc-in [i :trick-time] new-time-str)
                                      (swap! app-state* update :tricks
                                             assoc i (dissoc local-trick :trick-time)))))
                     :on-blur (fn [e]
                                (let [new-time (js/parseFloat trick-time)]
                                  (if-not (js/isNaN new-time)
                                    (swap! app-state* update :tricks
                                           assoc-in [i :trick-time] new-time)
                                    (js/console.log "ERROR"))))}
                    (when trick-time
                      {:value (str trick-time)}))]
     [:input (merge {:placeholder "Percent chance of success..."
                     :on-change (fn [e]
                                  (let [new-percent-str (-> e .-target .-value)]
                                    (if-not (empty? new-percent-str)
                                      (swap! app-state* update :tricks
                                             assoc-in [i :success-chance] new-percent-str)
                                      (swap! app-state* update :tricks
                                             assoc i (dissoc local-trick :success-chance)))))
                     :on-blur (fn [e]
                                (let [new-chance (js/parseFloat success-chance)]
                                  (if-not (js/isNaN new-chance)
                                    (swap! app-state* update :tricks
                                           assoc-in [i :success-chance] new-chance)
                                    (js/console.log "ERROR"))))}
                    (when success-chance
                      {:value (str success-chance)}))]
     [:button {:on-click #(swap! app-state* update :tricks
                                 (fn [tricks]
                                   (remove (fn [t]
                                             (= (:id t) id))
                                           tricks)))}
      "remove"]]))

(defn trick-area
  []
  (into [:div.tricks
         [:h2 "Tricks"]]
        (for [[i local-trick] (map-indexed vector (:tricks @app-state*))]
          ^{:key (:id local-trick)}
          [trick i local-trick])))

(defn- new-trick
  []
  {:id (random-uuid)})

(defn summary-area
  []
  (fn []
    [:div.summary
     [:h2 "Summary"]]))

(defn data-area
  []
  (fn []
    [:div.data
     [:h2 "Data"]
     [:div "Until I put a DB behind this, you can save and load data from here (Select All → Copy → Paste)..."]
     [:div [:textarea {:value (pr-str @app-state*)
                       :on-change #(reset! app-state*
                                           (cljs.reader/read-string (-> % .-target .-value)))}]]]))

(defn page
  []
  [:div.page
   [:h1 "Speedrun Strat Calculator"]
   [trick-area]
   [:button {:on-click #(swap! app-state* update :tricks conj (new-trick))}
    "add a trick"]
   [summary-area]
   [data-area]])

(defn ^:export main
  []
  (->> (.getElementById js/document "app")
       (reagent/render [page])))
