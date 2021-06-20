package nextstep.subway.line;

import static nextstep.subway.utils.CommonRequest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;

public class SectionAcceptanceTest extends AcceptanceTest {
    @BeforeEach
    public void setUp() {
        super.setUp();
        List<StationRequest> stationRequests = new ArrayList<>();

        stationRequests.add(new StationRequest("강남역"));
        stationRequests.add(new StationRequest("역삼역"));
        stationRequests.add(new StationRequest("신촌역"));
        stationRequests.add(new StationRequest("이대역"));

        for (StationRequest stationRequest : stationRequests) {
            ExtractableResponse<Response> response = post(stationRequest, "/stations");
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
        }
    }

    @DisplayName("노선 구간 등록 : 중간 (역과 역 사이)")
    @Test
    void addSectionInMiddle() {
        //given
        long upStationId = 1L;
        long downStationId = 2L;
        LineRequest lineRequest = createLineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);
        Long createId = response.jsonPath().getObject(".", LineResponse.class).getId();

        long newDownStationId = 3L;
        SectionRequest sectionRequest = new SectionRequest(upStationId, newDownStationId, 4);

        // when
        response = 지하철_노선에_지하철역_등록_요청(createId, sectionRequest);

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    private LineRequest createLineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }
    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
        return post(request, "/lines");
    }
    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long id, SectionRequest request) {
        return post(request, "/lines/" + id + "/sections");
    }
    private void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
