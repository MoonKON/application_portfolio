using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Right : MonoBehaviour
{
    [SerializeField] private float moveDistance = 5f; // 可在操作台更改的移动距离
    [SerializeField] private float speed = 2f;        // 可在操作台更改的速度

    private Vector3 startPosition;
    private bool movingRight = true;

    void Start()
    {
        // 记录物体的初始位置
        startPosition = transform.position;
    }

    void Update()
    {
        // 根据方向判断物体是向右还是向左移动
        if (movingRight)
        {
            // 向右移动
            transform.position = Vector3.MoveTowards(transform.position, startPosition + new Vector3(moveDistance, 0, 0), speed * Time.deltaTime);

            // 如果物体到达了目标距离，则改变方向为向左
            if (Vector3.Distance(transform.position, startPosition + new Vector3(moveDistance, 0, 0)) < 0.1f)
            {
                movingRight = false;
            }
        }
        else
        {
            // 向左移动（返回初始位置）
            transform.position = Vector3.MoveTowards(transform.position, startPosition, speed * Time.deltaTime);

            // 如果物体回到初始位置，则改变方向为向右
            if (Vector3.Distance(transform.position, startPosition) < 0.1f)
            {
                movingRight = true;
            }
        }
    }
}
