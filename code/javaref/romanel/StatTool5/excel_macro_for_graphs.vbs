Sub trend6()
    Dim ws As Worksheet
    Dim cpt, cptpoint As Long
    Dim color_index As Long
    For Each ws In ActiveWorkbook.Worksheets
        ActiveWorkbook.Activate
        ws.Select
        ws.Activate
        Worksheets.Add
        With ActiveSheet
            .Name = "G" + ws.Name
            Rem ch = .Controls.AddChart("B2:F7", "tagada")
        End With
        Charts.Add
        If ws.Name Like "*R" Then
        ActiveChart.ChartType = xlColumnStacked
        ActiveChart.SetSourceData Source:=Range(ws.Name), PlotBy:=xlColumns
        With ActiveChart
            .HasTitle = True
            .ChartTitle.Characters.Text = ws.Name
            
            cpt = 1
            For cpt = 1 To .SeriesCollection.Count
            Select Case cpt
            Case 1
            color_index = 1
            Case 2
            color_index = 9
            Case 3
            color_index = 13
            Case 4
            color_index = 54
            Case 5
            color_index = 45
            Case 6
            color_index = 43
            End Select
            .SeriesCollection(cpt).Interior.ColorIndex = color_index
            Next cpt
        End With
        ElseIf ws.Name Like "*X" Then
        ActiveChart.ChartType = xlCylinderColStacked100
        ActiveChart.SetSourceData Source:=Range(ws.Name), PlotBy:=xlColumns
        With ActiveChart
            .HasTitle = True
            .ChartTitle.Characters.Text = ws.Name
            cpt = 1
            For cpt = 1 To .SeriesCollection.Count
            Select Case cpt
            Case 1
            color_index = 16
            Case 2
            color_index = 53
            Case 3
            color_index = 43
            Case 4
            color_index = 33
            End Select
            .SeriesCollection(cpt).Interior.ColorIndex = color_index
            Next cpt
        End With
        ElseIf ws.Name Like "*M" Then
        ActiveChart.ChartType = xlAreaStacked
        ActiveChart.SetSourceData Source:=Range(ws.Name), _
            PlotBy:=xlColumns
        With ActiveChart
            .HasTitle = True
            .ChartTitle.Characters.Text = Range(ws.Name + "!$A$1").Value
            .Axes(xlCategory, xlPrimary).HasTitle = False
            .Axes(xlValue, xlPrimary).HasTitle = False
            cpt = 1
            For cpt = 1 To .SeriesCollection.Count
            ActiveChart.SeriesCollection(cpt).Select
            Selection.Fill.TwoColorGradient Style:=msoGradientHorizontal, Variant:=1
            With Selection
            .Fill.Visible = True
            Select Case cpt
            Case 1
                .Fill.ForeColor.SchemeColor = 4
                .Fill.BackColor.SchemeColor = 4
            Case 2
                .Fill.ForeColor.SchemeColor = 6
                .Fill.BackColor.SchemeColor = 6
            Case 3
                .Fill.ForeColor.SchemeColor = 3
                .Fill.BackColor.SchemeColor = 3
            Case 4
                .Fill.ForeColor.SchemeColor = 5
                .Fill.BackColor.SchemeColor = 5
            Case 5
                .Fill.ForeColor.SchemeColor = 43
                .Fill.BackColor.SchemeColor = 35
            Case 6
                .Fill.ForeColor.SchemeColor = 6
                .Fill.BackColor.SchemeColor = 36
            Case 7
                .Fill.ForeColor.SchemeColor = 3
                .Fill.BackColor.SchemeColor = 45
            Case 8
                .Fill.ForeColor.SchemeColor = 41
                .Fill.BackColor.SchemeColor = 8
            End Select
            End With
            Next cpt
            .SeriesCollection(5).ChartType = xlColumnClustered
            .SeriesCollection(5).Interior.ColorIndex = 9
            .SeriesCollection(5).ApplyDataLabels AutoText:=True, LegendKey:= _
                False, ShowSeriesName:=True, ShowCategoryName:=False, ShowValue:=False, _
                ShowPercentage:=False, ShowBubbleSize:=False
            .SeriesCollection(6).ChartType = xlColumnClustered
            .SeriesCollection(6).Interior.ColorIndex = 13
            .SeriesCollection(6).ApplyDataLabels AutoText:=True, LegendKey:= _
                False, ShowSeriesName:=True, ShowCategoryName:=False, ShowValue:=False, _
                ShowPercentage:=False, ShowBubbleSize:=False
        End With
        ElseIf ws.Name Like "*V" Then
        ActiveChart.ChartType = xlAreaStacked
        ActiveChart.SetSourceData Source:=Range(ws.Name), _
            PlotBy:=xlColumns
        With ActiveChart
            .HasTitle = True
            .ChartTitle.Characters.Text = Range(ws.Name + "!$A$1").Value
            .Axes(xlCategory, xlPrimary).HasTitle = False
            .Axes(xlValue, xlPrimary).HasTitle = False
            cpt = 1
            For cpt = 1 To .SeriesCollection.Count
            ActiveChart.SeriesCollection(cpt).Select
            Selection.Fill.TwoColorGradient Style:=msoGradientHorizontal, Variant:=1
            With Selection
            .Fill.Visible = True
            Select Case cpt
            Case 1
                .Fill.ForeColor.SchemeColor = 3
                .Fill.BackColor.SchemeColor = 46
            Case 2
                .Fill.ForeColor.SchemeColor = 45
                .Fill.BackColor.SchemeColor = 44
            Case 3
                .Fill.ForeColor.SchemeColor = 6
                .Fill.BackColor.SchemeColor = 35
            Case 4
                .Fill.ForeColor.SchemeColor = 4
                .Fill.BackColor.SchemeColor = 43
            End Select
            End With
            Next cpt
            .SeriesCollection(5).ChartType = xlColumnClustered
            .SeriesCollection(5).Interior.ColorIndex = 9
            .SeriesCollection(5).ApplyDataLabels AutoText:=True, LegendKey:= _
                False, ShowSeriesName:=True, ShowCategoryName:=False, ShowValue:=False, _
                ShowPercentage:=False, ShowBubbleSize:=False
            .SeriesCollection(6).ChartType = xlColumnClustered
            .SeriesCollection(6).Interior.ColorIndex = 13
            .SeriesCollection(6).ApplyDataLabels AutoText:=True, LegendKey:= _
                False, ShowSeriesName:=True, ShowCategoryName:=False, ShowValue:=False, _
                ShowPercentage:=False, ShowBubbleSize:=False
        End With
        Else
        ActiveChart.ChartType = xlAreaStacked
        ActiveChart.SetSourceData Source:=Range(ws.Name), _
            PlotBy:=xlColumns
        With ActiveChart
            .HasTitle = True
            .ChartTitle.Characters.Text = ws.Name
            .Axes(xlCategory, xlPrimary).HasTitle = False
            .Axes(xlValue, xlPrimary).HasTitle = False
            cpt = 1
            For cpt = 1 To .SeriesCollection.Count
            Select Case cpt
            Case 1
            color_index = 1
            Case 2
            color_index = 9
            Case 3
            color_index = 13
            Case 4
            color_index = 54
            Case 5
            color_index = 45
            Case 6
            color_index = 43
            End Select
            .SeriesCollection(cpt).Interior.ColorIndex = color_index
            Next cpt
        End With
        End If
        ActiveChart.Location Where:=xlLocationAsObject, Name:="G" + ws.Name
        With Worksheets("G" + ws.Name)
            .ChartObjects(1).Width = 900
            .ChartObjects(1).Height = 600
            .ChartObjects(1).Left = .Columns("A").Left
            .ChartObjects(1).Top = .Rows("2").Top
        End With
    Next ws
    
    Dim wsTOC As Worksheet
    Dim Chart As Chart
    Dim r As Long
    Application.ScreenUpdating = False
    Set wsTOC = ActiveWorkbook.Worksheets.Add _
         (Before:=ActiveWorkbook.Sheets(1))
    wsTOC.Name = "Table_of_Contents"
    wsTOC.Range("A1") = "Table of Contents"
    wsTOC.Range("A1").Font.Size = 18
    wsTOC.Columns("A:A").ColumnWidth = 40
    r = 3
    For Each ws In ActiveWorkbook.Worksheets
        If ws.Name <> wsTOC.Name And ws.Name Like "G*" Then
            wsTOC.Hyperlinks.Add _
                anchor:=wsTOC.Cells(r, 1), _
                Address:="", _
                SubAddress:=ws.Name & "!A1", _
                TextToDisplay:=ws.Name
            r = r + 1
            ws.Hyperlinks.Add _
                anchor:=ws.Cells(1, 1), _
                Address:="", _
                SubAddress:="Table_of_Contents!A1", _
                TextToDisplay:="Table of Contents"
        End If
    Next
    Application.ScreenUpdating = True
End Sub




