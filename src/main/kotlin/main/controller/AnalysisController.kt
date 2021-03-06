package main.controller

import com.fasterxml.jackson.annotation.JsonView
import main.service.AnalysisService
import main.util.Endpoints
import main.util.ResponseFactory
import main.util.RestResponse
import main.util.Views
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Endpoints.ANALYSIS)
class AnalysisController(
    val analysisService: AnalysisService
) {
    @GetMapping("{key}")
    @JsonView(Views.Minimal::class)
    fun getAnalysis(
        @PathVariable key: String
    ): RestResponse {
        return ResponseFactory.ok("analysis", analysisService.get(key))
    }

    @PostMapping("{key}")
    @JsonView(Views.Minimal::class)
    fun startAnalysis(
        @PathVariable key: String
    ): RestResponse {
        analysisService.start(key)
        return ResponseFactory.ok()
    }
}