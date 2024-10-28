package com.common.service.Email;

import com.common.pojo.Result;

public interface EmailService {
     Result emailVerification(String to);
}
