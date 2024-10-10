package mouda.backend;

import groovy.transform.builder.InitializerStrategy.SET;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import mouda.backend.darakbang.implement.InvitationCodeGenerator;
import mouda.backend.moim.domain.Moim;
import mouda.backend.moim.infrastructure.MoimRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
public class TestDataUtil {

    @Autowired
    private MoimRepository moimRepository;

    @Autowired
    private InvitationCodeGenerator invitationCodeGenerator;

    @Test
    public void generateInsertStatementsForH2() {

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

//        StringBuilder darakbang = new StringBuilder("INSERT INTO darakbang (name, code) VALUES ");
//        for (int i = 0; i < 10000; i++) {
//            darakbang.append(String.format("('다락방%d', '%s')", i, invitationCodeGenerator.generate()));
//            if (i != 9999) {
//                darakbang.append(",\n");
//            } else {
//                darakbang.append(";\n");
//            }
//        }

        StringBuilder moim = new StringBuilder(
                "INSERT INTO moim (title, date, time, place, max_people, description, darakbang_id, moim_status, is_chat_opened) VALUES ");
        int initialDarakbangId = 1;
        for (int i = 0; i < 700000; i++) {
            if (i >= 70 && i % 70 == 0) {
                initialDarakbangId++;
            }
            moim.append(String.format("('모임%d', '%s', '%s', '모임%d 장소', %d, '모임%d 설명', '%d', 'MOIMING', 0)",
                    (300000 + i),
                    date.plusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    time.plusMinutes(i).format(DateTimeFormatter.ofPattern("HH:mm")),
                    (300000 + i),
                    new Random().nextInt(1, 20),
                    (300000 + i),
                    initialDarakbangId
            ));
            if (i != 699999) {
                moim.append(",\n");
            } else {
                moim.append(";\n");
            }
        }

        // 파일로 저장
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("test.sql"))) {
//            writer.write(darakbang.toString());
            writer.write(moim.toString());
            System.out.println("SQL file has been generated successfully.");
        } catch (IOException e) {
            System.err.println("Error while writing to file: " + e.getMessage());
        }

    }

    @Sql("/test.sql")
    @Test
    void found() {
        List<Moim> moims = moimRepository.findAll();
        System.out.println(moims.size());
    }
}
