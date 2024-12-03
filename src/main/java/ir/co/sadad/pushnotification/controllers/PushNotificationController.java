package ir.co.sadad.pushnotification.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.co.sadad.pushnotification.dtos.*;
import ir.co.sadad.pushnotification.services.FirebaseCloudMessagingService;
import ir.co.sadad.pushnotification.services.PushUserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author g.shahrokhabadi
 * @version 11, 1401/09/20
 * <p>
 * This class includes entry points of services for add or update user information, active and deactivate sending push for them
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class PushNotificationController {


}
