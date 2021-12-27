package com.dean.pr_po

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dean.pr_po.databinding.ActivityDetailBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    private lateinit var detailBinding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)

        val person = intent.getParcelableExtra<UserData>(EXTRA_DATA) as UserData
        detailBinding.tvItemName.text = person.name

        barChart.description.isEnabled = false
        barChart.xAxis?.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis?.granularity = 1f
        barChart.xAxis?.setCenterAxisLabels(true)
        barChart.xAxis?.setDrawGridLines(true)
        barChart.xAxis?.spaceMin = 0f

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.spaceTop = 10f
        barChart.axisLeft.setDrawGridLines(true)
        barChart.axisRight.isEnabled = false

        val legend = barChart.legend
        legend.isEnabled = true
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER)
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        legend.setDrawInside(true)

        val prMonthAgo = person.prMonthAgo?.toFloat()
        val prLastMonth = person.prLastMonth?.toFloat()
        val prThisMonth = person.prThisMonth?.toFloat()

        val dataPo = ArrayList<BarEntry>()
        prMonthAgo?.let { BarEntry(0F, it) }?.let { dataPo.add(it) }
        prLastMonth?.let { BarEntry(1F, it) }?.let { dataPo.add(it) }
        prThisMonth?.let { BarEntry(2F, it) }?.let { dataPo.add(it) }
        dataPo.add(BarEntry(3F, 0F))

        val befMonthAgo = person.poMonthAgo
        val befLastMonth = person.poLastMonth
        val befThisMonth = person.poThisMonth
        val poMonthAgo = befMonthAgo?.toFloat()
        val poLastMonth = befLastMonth?.toFloat()
        val poThisMonth = befThisMonth?.toFloat()
        val dataPr = ArrayList<BarEntry>()
        poMonthAgo?.let { BarEntry(0F, it) }?.let { dataPr.add(it) }
        poLastMonth?.let { BarEntry(1F, it) }?.let { dataPr.add(it) }
        poThisMonth?.let { BarEntry(2F, it) }?.let { dataPr.add(it) }
        dataPr.add(BarEntry(3F, 0F))

        val prBarDataSet = BarDataSet(dataPo, "PR")
        prBarDataSet.color = Color.BLUE

        val poBarDataSet = BarDataSet(dataPr, "PO")
        poBarDataSet.color = Color.GREEN

        val date = ArrayList<String>();
        date.add(">3-Bulan")
        date.add("Bulan-Lalu")
        date.add("Bulan-Ini")
        date.add("-")
        val tanggal = AxisDateFormatter(date.toArray(arrayOfNulls<String>(date.size)))
        barChart.xAxis?.setValueFormatter(tanggal)

        val groupSpace = 0.5f
        val barSpace = 0f
        val barWidth = 0.27f
        val groupBar = BarData(prBarDataSet, poBarDataSet)
        groupBar.barWidth = barWidth
        barChart.data = groupBar
        barChart.groupBars(0f, groupSpace, barSpace)
        barChart.animateXY(100, 500)
    }
}