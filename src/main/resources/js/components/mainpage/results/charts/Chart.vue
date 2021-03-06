<template>
    <div>
        <v-dialog v-if="selectedItem" v-model="showDialog" width="300">
            <v-color-picker v-model="selectedItem.color"/>
        </v-dialog>

        <v-card class="ma-4 mt-0" outlined>
            <v-card-title>
                <v-combobox
                    v-model="selectedChart"
                    :items="['Кольцевая диаграмма', 'Круговая диаграмма', 'Столбчатая диаграмма']"
                    single-line dense solo hide-details
                />
                <v-spacer/>
                <v-btn text @click="recolor()">
                    перекрасить
                </v-btn>
            </v-card-title>

            <v-divider/>

            <div :id="`chart-${id}`">
                <v-card-title class="py-0">
                    {{ getTruncated(title) }}
                </v-card-title>
                <v-row class="ma-0 pa-0">
                    <svg :width="325"
                         :height="animatedChartData.length*30">
                        <g v-for="(item, index) in animatedChartData">
                            <text @mouseover="focusItem(item)"
                                  @mouseleave="resetColors()"
                                  font-weight="bold"
                                  :y="index*30+4.2*5"
                                  :x="35"
                                  style="fill: black">
                                {{ item.text }} ({{ item.value }} {{ getLocalizedText(item.value) }})
                            </text>
                            <rect @click="showPicker(defaultColors[index])"
                                  @mouseover="focusItem(item)"
                                  @mouseleave="resetColors()"
                                  :height="25" :width="25"
                                  :x="5"
                                  :y="index*30+5"
                                  :style="'transform-origin: center; ' +
                                   'transform: scale('+animatedChartData[index].scale+', '+animatedChartData[index].scale+'); ' +
                                    'fill:'+getRgb(animatedChartData[index].color)+';' +
                                     'stroke-width:1;stroke:rgb(0,0,0)'"/>
                        </g>
                    </svg>

                    <bar-chart v-if="selectedChart==='Столбчатая диаграмма'"
                               @mouseleavechild="handleMouseLeaveChild" @mouseoverchild="handleMouseOverChild"
                               :chart-data="animatedChartData"
                               :total-results="totalResults"/>
                    <pie-chart v-if="selectedChart==='Кольцевая диаграмма' || selectedChart==='Круговая диаграмма'"
                               @mouseleavechild="handleMouseLeaveChild" @mouseoverchild="handleMouseOverChild"
                               :chart-data="animatedChartData"
                               :is-ring="selectedChart==='Кольцевая диаграмма'"
                               :total-results="totalResults"/>
                </v-row>
            </div>
        </v-card>
    </div>
</template>

<script>
import html2canvas from "html2canvas"
import BarChart from "./BarChart.vue"
import PieChart from "./PieChart.vue"
import util from "../../../../use/util";

export default {
    components: {PieChart, BarChart},
    props: ['chartData', 'totalResults', 'id', 'title'],
    data() {
        return {
            selectedItem: undefined,
            showDialog: false,
            defaultColors: [],
            animatedChartData: [],
            selectedChart: 'Кольцевая диаграмма'
        }
    },
    watch: {
        showDialog: function (value) {
            if (!value)
                this.resetColors()
        }
    },
    created() {
        for (let i = 0; i < this.chartData.length; i++) {
            let vue = this;
            this.animatedChartData.push({
                value: vue.chartData[i].results,
                text: vue.chartData[i].value,
                color: undefined,
                scale: 1
            })
        }
        this.fillColors()
    },
    methods: {
        fillColors() {
            this.animatedChartData.sort((a, b) => a.value > b.value ? -1 : 1)
            let sum = 0
            for (let i = 0; i < this.animatedChartData.length; i++)
                sum += this.animatedChartData[i].value
            let accumSum = 0
            for (let i = 0; i < this.animatedChartData.length; i++) {
                let colorPercent = (accumSum + (this.animatedChartData[i].value / 2)) / sum
                let color = Math.floor(colorPercent * 255)
                let colorObject = {
                    r: 0,
                    g: color * 0.80,
                    b: color
                }
                this.animatedChartData[i].color = colorObject
                this.defaultColors.push({color: colorObject})
                accumSum += this.animatedChartData[i].value
            }
        },
        recolor() {
            for (let i = 0; i < this.defaultColors.length; i++) {
                this.defaultColors[i].color = {
                    r: Math.floor(Math.random() * 255),
                    g: Math.floor(Math.random() * 255),
                    b: Math.floor(Math.random() * 255),
                }
            }
            this.resetColors()
        },
        getTruncated(text){
            return util.truncate(text, 80)
        },
        handleMouseLeaveChild() {
            this.resetColors()
        },
        handleMouseOverChild(item) {
            this.focusItem(item)
        },
        getImg(scale) {
            let chart = document.getElementById(`chart-${this.id}`)
            return html2canvas(chart, {imageTimeout: 0, scale: scale, scrollX: 0, scrollY: -window.scrollY})
        },
        getRgb(item) {
            return 'rgb(' + item.r + ',' + item.g + ',' + item.b + ')'
        },
        resetColors() {
            for (let i = 0; i < this.defaultColors.length; i++) {
                this.animatedChartData[i].scale = 1
                this.animatedChartData[i].color = this.defaultColors[i].color
            }
        },
        showPicker(item) {
            this.showDialog = true
            this.selectedItem = item
        },
        focusItem(item) {
            for (let i = 0; i < this.animatedChartData.length; i++) {
                if (this.animatedChartData[i].color === item.color) {
                    this.animatedChartData[i].scale = 1.015
                } else {
                    this.animatedChartData[i].color = {
                        r: 188,
                        g: 188,
                        b: 188
                    }
                }
            }
        },
        getTextPercent(value, data) {
            let sum = 0
            for (let i = 0; i < data.length; i++) {
                sum += data[i].value
            }
            return Math.round(value / sum * 10000) / 100 + '%'
        },
        getLocalizedText(amount) {
            let stringSum = amount.toString()
            let lastNum = stringSum.charAt(stringSum.length - 1)

            if (stringSum.length > 1 && stringSum.charAt(stringSum.length - 2) === '1')
                return 'респондентов'
            if (lastNum === '1')
                return 'респондент'
            if (['2', '3', '4'].includes(lastNum))
                return 'респондента'
            if (['0', '5', '6', '7', '8', '9'].includes(lastNum))
                return 'респондентов'
        }
    },
}
</script>

<style scoped>
</style>