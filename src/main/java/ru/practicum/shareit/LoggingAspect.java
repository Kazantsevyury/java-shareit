package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* ru.practicum.shareit.*.*.*(..))")
    public Object logAroundMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName().replaceAll("ru.practicum.shareit.", "");
        String methodName = joinPoint.getSignature().getName();

        log.info("Вход в метод: {}() с аргументами = {}", className + "." + methodName, Arrays.toString(joinPoint.getArgs()));
        try {
            Object result = joinPoint.proceed();
            log.info("Выход из метода: {}() с результатом = {}", className + "." + methodName, result);
            return result;
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Некорректный аргумент: {} в {}()", Arrays.toString(joinPoint.getArgs()), className + "." + methodName);
            throw e;
        }
    }
}
